package org.pockito.validation;

import org.junit.runners.model.InitializationError;

import java.util.List;

public class PockitoValidationException extends InitializationError {

    public PockitoValidationException(List<Throwable> errors) {
        super(errors);
    }

    public PockitoValidationException(Throwable error) {
        super(error);
    }

    public PockitoValidationException(String string) {
        super(string);
    }
}
