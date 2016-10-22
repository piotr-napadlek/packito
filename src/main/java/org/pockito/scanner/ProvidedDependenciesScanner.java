package org.pockito.scanner;

public interface ProvidedDependenciesScanner {
    DependenciesContainer getProvidedDependencies(Object testClassInstance) throws PockitoInitializationException;
}
