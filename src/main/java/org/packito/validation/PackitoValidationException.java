package org.packito.validation;

import org.junit.runners.model.InitializationError;

import java.util.List;

public class PackitoValidationException extends InitializationError {

    public PackitoValidationException(List<Throwable> errors) {
        super(errors);
    }

    public PackitoValidationException(Throwable error) {
        super(error);
    }

    public PackitoValidationException(String string) {
        super(string);
    }
}
