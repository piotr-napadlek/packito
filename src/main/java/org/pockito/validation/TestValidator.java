package org.pockito.validation;

public interface TestValidator {
    void validate(Class<?> klass) throws PockitoValidationException;
}
