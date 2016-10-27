package org.packito.testscanner.util;

import org.packito.testscanner.PackitoInitializationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ScannerUtils {

    private ScannerUtils() {
    }

    public static void iterateClassHierarchy(Class<?> klass, Consumer<Class> klassConsumer) {
        Class scannedClass = klass;
        while (!isObject(scannedClass)) {
            klassConsumer.accept(scannedClass);
            scannedClass = (scannedClass == klass.getSuperclass()) ? null : klass.getSuperclass();
        }
    }

    public static List<Field> findAllFieldsInHierarchy(Class<?> klass) {
        final List<Field> allFields = new ArrayList<>();
        iterateClassHierarchy(klass, k -> allFields.addAll(Arrays.asList(k.getDeclaredFields())));
        return allFields;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T findAnnotationInClassHierarchy(Class<?> klass, Class<T> searchedAnnotation) {
        List<Annotation> foundAnnotations = new ArrayList<>();
        iterateClassHierarchy(klass, aClass -> foundAnnotations.add(aClass.getDeclaredAnnotation(searchedAnnotation)));
        return (T) foundAnnotations.stream().filter(Objects::nonNull)
                .findFirst().orElseThrow(() -> new PackitoInitializationException("No annotation of type " + searchedAnnotation + " was found."));
    }

    private static boolean isObject(Class scannedClass) {
        return scannedClass == null || scannedClass.equals(Object.class);
    }
}
