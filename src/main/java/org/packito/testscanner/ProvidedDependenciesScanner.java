package org.packito.testscanner;

public interface ProvidedDependenciesScanner {
    DependenciesContainer getProvidedDependencies(Object testClassInstance) throws PackitoInitializationException;
}
