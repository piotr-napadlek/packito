package org.pockito.scanner;

import org.pockito.annotations.MockProvider;
import org.pockito.scanner.util.ScannerUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class PockitoProvidedDependenciesScanner implements ProvidedDependenciesScanner {

    @Override
    public DependenciesContainer getProvidedDependencies(Object testClassInstance) throws PockitoInitializationException {
        DependenciesContainer container = new DependenciesContainer();
        container.setMockProvider(findMockProvider(testClassInstance));
        return container;
    }

    private Function<Class<?>, ?> findMockProvider(Object testClassInstance) throws PockitoInitializationException {
        List<Field> testedClassFields = new ArrayList<>();

        ScannerUtils.iterateClassHierarchy(testClassInstance.getClass(), k -> testedClassFields.addAll(Arrays.asList(k.getDeclaredFields())));
        Field mockProviderField = testedClassFields.stream().filter(field -> field.getAnnotation(MockProvider.class) != null).findFirst().orElse(null);
        Function<Class<?>, ?> mockProvider = null;
        if (mockProviderField != null) {
            try {
                mockProviderField.setAccessible(true);
                mockProvider = (Function<Class<?>, ?>) mockProviderField.get(testClassInstance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new PockitoInitializationException("Could not access field marked as @MockProvider");
            } catch (ClassCastException e) {
                e.printStackTrace();
                throw new PockitoInitializationException("Field marked as @MockProvider is not of correct type."
                                                                 + "Correct type for a mock provider is Function<Class<?>, ?>");
            } finally {
                mockProviderField.setAccessible(false);
            }
        }
        return mockProvider;
    }
}
