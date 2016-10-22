package org.pockito.scanner;

import org.pockito.scanner.util.MultiMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DependenciesContainer {
    private Function<Class<?>, ?> mockProvider;
    private MultiMap<Class<?>, ?> providedDependencies;
    private Map<Class<?>, Supplier<?>> dependencyProviders;
    private List<Class<?>> classesToMock;

    public DependenciesContainer() {

    }

    public Function<Class<?>, ?> getMockProvider() {
        return mockProvider;
    }

    public void setMockProvider(Function<Class<?>, ?> mockProvider) {
        this.mockProvider = mockProvider;
    }

    public MultiMap<Class<?>, ?> getProvidedDependencies() {
        return providedDependencies;
    }

    public void setProvidedDependencies(MultiMap<Class<?>, ?> providedDependencies) {
        this.providedDependencies = providedDependencies;
    }

    public Map<Class<?>, Supplier<?>> getDependencyProviders() {
        return dependencyProviders;
    }

    public void setDependencyProviders(Map<Class<?>, Supplier<?>> dependencyProviders) {
        this.dependencyProviders = dependencyProviders;
    }

    public List<Class<?>> getClassesToMock() {
        return classesToMock;
    }

    public void setClassesToMock(List<Class<?>> classesToMock) {
        this.classesToMock = classesToMock;
    }
}
