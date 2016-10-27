package org.packito.validation;

public interface TestValidator {
    void validate(Class<?> klass) throws PackitoValidationException;
}
