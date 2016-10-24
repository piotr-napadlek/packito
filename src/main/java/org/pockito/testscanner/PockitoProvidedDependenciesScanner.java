package org.pockito.testscanner;

import org.pockito.annotations.DependencyProvider;
import org.pockito.annotations.MockProvider;
import org.pockito.annotations.MockedDependency;
import org.pockito.annotations.ProvidedDependency;
import org.pockito.testscanner.util.ScannerUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class PockitoProvidedDependenciesScanner implements ProvidedDependenciesScanner {

    @Override
    public DependenciesContainer getProvidedDependencies(Object testClassInstance) throws PockitoInitializationException {
        DependenciesContainer container = new DependenciesContainer();
        List<Field> testedClassFields = ScannerUtils.findAllFieldsInHierarchy(testClassInstance.getClass());
        Function<Class<?>, Object> mockProvider = findMockProvider(testClassInstance, testedClassFields);
        container.setMockProvider(mockProvider);
        container.setDependencyProviders(findDependencyProviders(testClassInstance, testedClassFields));
        container.setProvidedDependencies(findProvidedDependencies(testClassInstance, testedClassFields));
        container.setMockedClasses(findClassesToMock(testClassInstance, testedClassFields, mockProvider));
        return container;
    }

    private Map<Class<?>, Object> findClassesToMock(Object testClassInstance, List<Field> testedClassFields,
                                                     Function<Class<?>, ?> mockProvider) throws PockitoInitializationException {
        List<Field> annotatedFields = getFieldsWithAnnotation(testedClassFields, MockedDependency.class);
        Map<Class<?>, Object> classesToMock = new HashMap<>();
        for (Field field : annotatedFields) {
            try {
                field.setAccessible(true);
                if (mockProvider != null && field.getDeclaredAnnotation(MockedDependency.class).autoMock()) {
                    field.set(testClassInstance, mockProvider.apply(field.getType()));
                }
                classesToMock.put(field.getType(), field.get(testClassInstance));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new PockitoInitializationException("Field with the name " + field.getName() + " could not be accessed.");
            } finally {
                field.setAccessible(false);
            }
        }
        return classesToMock;
    }

    private Map<Class<?>, Object> findProvidedDependencies(Object testClassInstance, List<Field> testedClassFields) throws PockitoInitializationException {
        Map<Class<?>, Object> providedDependencies = new HashMap<>();
        List<Field> annotatedFields = getFieldsWithAnnotation(testedClassFields, ProvidedDependency.class);
        for (Field field : annotatedFields) {
            try {
                field.setAccessible(true);
                Object dependency = field.get(testClassInstance);
                providedDependencies.put(field.getType(), dependency);
            } catch (IllegalAccessException e) {
                throw new PockitoInitializationException("Field with the name " + field.getName() + " could not be accessed.");
            } catch (NullPointerException npe) {
                throw new PockitoInitializationException("Field with the name " + field.getName() + " marked as @ProvidedDependency was null "
                                                                 + "(and it's not allowed to be null)");
            } finally {
                field.setAccessible(false);
            }
        }
        return providedDependencies;
    }

    private Map<Class<?>, Supplier<?>> findDependencyProviders(Object testClassInstance, List<Field> testedClassFields) throws PockitoInitializationException {
        Map<Class<?>, Supplier<?>> dependencyProviders = new HashMap<>();
        List<Field> annotatedFields = getFieldsWithAnnotation(testedClassFields, DependencyProvider.class);
        for (Field field : annotatedFields) {
            try {
                field.setAccessible(true);
                Supplier<?> dependencyProvider = (Supplier<?>) field.get(testClassInstance);
                dependencyProviders.put(dependencyProvider.get().getClass(), dependencyProvider);
            } catch (IllegalAccessException e) {
                throw new PockitoInitializationException("Field with the name " + field.getName() + " could not be accessed.");
            } catch (ClassCastException e) {
                throw new PockitoInitializationException("Field with the name " + field.getName() + " is of incorrect type." +
                                                                 " Proper type for a @DependencyProvider is Supplier<?>");
            } catch (NullPointerException npe) {
                throw new PockitoInitializationException("Field with the name " + field.getName() + " marked as @DependencyProvider was null "
                                                                 + "(and it's not allowed to be null)");
            } finally {
                field.setAccessible(false);
            }
        }
        return dependencyProviders;
    }

    private Function<Class<?>, Object> findMockProvider(Object testClassInstance, List<Field> testedClassFields) throws PockitoInitializationException {
        Field mockProviderField = getFieldsWithAnnotation(testedClassFields, MockProvider.class).stream()
                .findFirst().orElse(null);
        Function<Class<?>, Object> mockProvider = null;
        if (mockProviderField != null) {
            try {
                mockProviderField.setAccessible(true);
                mockProvider = (Function<Class<?>, Object>) mockProviderField.get(testClassInstance);
            } catch (IllegalAccessException e) {
                throw new PockitoInitializationException("Could not access field marked as @MockProvider");
            } catch (ClassCastException e) {
                throw new PockitoInitializationException("Field marked as @MockProvider is not of correct type."
                                                                 + "Correct type for a mock provider is Function<Class<?>, ?>");
            } finally {
                mockProviderField.setAccessible(false);
            }
        }
        return mockProvider;
    }

    private List<Field> getFieldsWithAnnotation(List<Field> testedClassFields, Class klass) {
        List<Field> foundFields = new ArrayList<>();
        testedClassFields.forEach(field -> {
            if (field.getDeclaredAnnotation(klass) != null) {
                foundFields.add(field);
            }
        });
        return foundFields;
    }
}
