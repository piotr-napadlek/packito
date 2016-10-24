package org.pockito.testscanner;

public interface ProvidedDependenciesScanner {
    DependenciesContainer getProvidedDependencies(Object testClassInstance) throws PockitoInitializationException;
}
