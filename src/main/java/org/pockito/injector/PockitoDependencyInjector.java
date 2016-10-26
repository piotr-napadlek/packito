package org.pockito.injector;

import org.pockito.testscanner.ClassToTest;
import org.pockito.testscanner.DependenciesContainer;
import org.pockito.testscanner.PockitoInitializationException;
import org.pockito.testscanner.util.ScannerUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PockitoDependencyInjector {
    private static final List<String> SUPPORTED_ANNOTATIONS = new ArrayList<>();

    static {
        SUPPORTED_ANNOTATIONS.add("javax.inject.Inject");
        SUPPORTED_ANNOTATIONS.add("org.springframework.beans.factory.annotation.Autowired");
    }

    public void injectDependenciesDeep(ClassToTest classToTest, List<Class<?>> classesInAPackage, DependenciesContainer dependencies) {
        try {
            if (classToTest.isAutoInstantiate() && classToTest.getInstance() == null) {
                Constructor<?> klassConstructor = Stream.of(classToTest.getKlass().getConstructors()).filter(c -> c.getParameterCount() == 0).findFirst()
                        .orElseThrow(NotImplementedException::new);// we rely temporarily on parameterless constructors
                classToTest.setInstance(klassConstructor.newInstance());
                classToTest.getInjectionField().setAccessible(true);
                classToTest.getInjectionField().set(classToTest.getContainingTestClass(), classToTest.getInstance());
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new PockitoInitializationException(e.toString());
        }

        injectDependencies(classToTest.getInstance(), classesInAPackage, dependencies);
    }

    private void injectDependencies(Object instance, List<Class<?>> classesInAPackage, DependenciesContainer dependencies) {
        Class<?> klass = instance.getClass();
        List<Field> fieldsToInject = ScannerUtils.findAllFieldsInHierarchy(klass).stream()
                .filter(field -> Stream.of(field.getDeclaredAnnotations()).map(a -> a.annotationType().getName()).anyMatch(SUPPORTED_ANNOTATIONS::contains))
                .collect(Collectors.toList());
        for (Field field : fieldsToInject) {
            field.setAccessible(true);
            try {
                tryToInjectProvidedDependency(instance, dependencies, field);
                tryToUseDependencyProvider(instance, dependencies, field);
                tryToInjectAlreadyInstantiatedDependencyFromTestedPackage(instance, dependencies, field);
                tryToInstantiateIfInPackageOrMockIfOutside(instance, classesInAPackage, dependencies, field);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                throw new PockitoInitializationException("Could not inject field " + field.getName() + " of type " + field.getType()
                                                                 + " in class " + klass.getName());
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new PockitoInitializationException("Could not instantiate class for a field " + field.getName() + " of type " + field.getType()
                                                                 + " in class " + klass.getName());
            }
        }
    }

    private void tryToInstantiateIfInPackageOrMockIfOutside(Object instance, List<Class<?>> classesInAPackage, DependenciesContainer dependencies,
                                                            Field field) throws IllegalAccessException, InstantiationException {
        if (field.get(instance) == null) { // still didn't succeed to provide a dependency
            if (isAssignableFromAny(field.getType(), classesInAPackage)) {  // at last we just try to instantiate
                tryToInstantiate(instance, classesInAPackage, dependencies, field);
            } else {
                injectMock(instance, dependencies, field);
            }
        }
    }

    private void injectMock(Object instance, DependenciesContainer dependencies, Field field) throws IllegalAccessException {
        Optional<Class<?>> mockedAssignableClass = getAssignableClass(field.getType(), dependencies.getMockedClasses().keySet());
        if (mockedAssignableClass.isPresent()) {
            field.set(instance, dependencies.getMockedClasses().get(mockedAssignableClass.get())); // inject declared mock
        } else {
            field.set(instance, dependencies.getMockProvider().apply(field.getType())); // finally we inject an anonymous mock
        }
    }

    private void tryToInstantiate(Object instance, List<Class<?>> classesInAPackage, DependenciesContainer dependencies,
                                  Field field) throws InstantiationException, IllegalAccessException {
        Object realInstanceFromPackage = getAssignableClass(field.getType(), classesInAPackage).get().newInstance();
        field.set(instance, realInstanceFromPackage);
        dependencies.addInstantiatedClass(realInstanceFromPackage);
        injectDependencies(realInstanceFromPackage, classesInAPackage, dependencies);
    }

    private void tryToInjectAlreadyInstantiatedDependencyFromTestedPackage(Object instance, DependenciesContainer dependencies,
                                                                           Field field) throws IllegalAccessException {
        if (field.get(instance) == null && isAssignableFromAny(field.getType(), dependencies.getInstantiatedClasses().keySet())) {
            field.set(instance, dependencies.getInstantiatedClasses().get(
                    getAssignableClass(field.getType(), dependencies.getInstantiatedClasses().keySet()).get()));
        }
    }

    private void tryToUseDependencyProvider(Object instance, DependenciesContainer dependencies, Field field) throws IllegalAccessException {
        Optional<Class<?>> supplierAssignableClass = getAssignableClass(field.getType(), dependencies.getDependencyProviders().keySet());
        if (field.get(instance) == null && supplierAssignableClass.isPresent()) {
            field.set(instance, dependencies.getDependencyProviders().get(supplierAssignableClass.get()).get()); // get get get ! :)
        }
    }

    private void tryToInjectProvidedDependency(Object instance, DependenciesContainer dependencies, Field field) throws IllegalAccessException {
        Optional<Class<?>> providedAssignableClass = getAssignableClass(field.getType(), dependencies.getProvidedDependencies().keySet());
        if (providedAssignableClass.isPresent()) { // provided instance is preferred over dependency supplier
            field.set(instance, dependencies.getProvidedDependencies().get(providedAssignableClass.get()));
        }
    }

    private boolean isAssignableFromAny(Class<?> leftSideOfAssignment, Collection<Class<?>> potentialRightSides) {
        return potentialRightSides.stream().anyMatch(leftSideOfAssignment::isAssignableFrom);
    }

    private Optional<Class<?>> getAssignableClass(Class<?> targetClass, Collection<Class<?>> potentialClasses) {
        List<Class<?>> matchedClasses = potentialClasses.stream().filter(targetClass::isAssignableFrom).collect(Collectors.toList());
        if (matchedClasses.size() > 1) {
            throw new PockitoInitializationException("There is ambiguity for injection of field of type " + targetClass.getName()
                                                             + ", matching classes are " + matchedClasses.toString());
        }
        return matchedClasses.stream().findAny();
    }
}
