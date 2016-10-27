package org.packito.testscanner;

import org.packito.annotations.TestedClass;
import org.packito.testscanner.util.ScannerUtils;

import java.lang.reflect.Field;

public class TestClassScanner {

    public static ClassToTest findClassToTest(Object testedClassInstance) {
        Field testedClassField = ScannerUtils.findAllFieldsInHierarchy(testedClassInstance.getClass())
                .stream().filter(field -> field.getDeclaredAnnotation(TestedClass.class) != null)
                .findFirst().orElse(null);
        ClassToTest classToTest = new ClassToTest();
        classToTest.setKlass(testedClassField.getType());
        try {
            testedClassField.setAccessible(true);
            classToTest.setInstance(testedClassField.get(testedClassInstance));
            classToTest.setAutoInstantiate(testedClassField.getDeclaredAnnotation(TestedClass.class).autoInstantiate());
            classToTest.setInjectionField(testedClassField);
            classToTest.setContainingTestClass(testedClassInstance);
        } catch (IllegalAccessException e) {
            throw new PackitoInitializationException("@TestedClass could not be accessed.");
        } finally {
            testedClassField.setAccessible(false);
        }
        return classToTest;
    }
}
