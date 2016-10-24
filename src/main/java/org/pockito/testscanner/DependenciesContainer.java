package org.pockito.testscanner;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DependenciesContainer {
    private Function<Class<?>, Object> mockProvider;
    private Map<Class<?>, Object> providedDependencies;
    private Map<Class<?>, Supplier<?>> dependencyProviders;
    private Map<Class<?>, Object> mockedClasses;

    public DependenciesContainer() {}

    public Function<Class<?>, Object> getMockProvider() {
        return mockProvider;
    }

    public void setMockProvider(Function<Class<?>, Object> mockProvider) {
        this.mockProvider = mockProvider;
    }

    public Map<Class<?>, ?> getProvidedDependencies() {
        return providedDependencies;
    }

    public void setProvidedDependencies(Map<Class<?>, Object> providedDependencies) {
        this.providedDependencies = providedDependencies;
    }

    public Map<Class<?>, Supplier<?>> getDependencyProviders() {
        return dependencyProviders;
    }

    public void setDependencyProviders(Map<Class<?>, Supplier<?>> dependencyProviders) {
        this.dependencyProviders = dependencyProviders;
    }

    public Map<Class<?>, Object> getMockedClasses() {
        return mockedClasses;
    }

    public void setMockedClasses(Map<Class<?>, Object> mockedClasses) {
        this.mockedClasses = mockedClasses;
    }
}
