package org.pockito.scanner;

import org.junit.runners.model.InitializationError;

import java.util.List;

public class PockitoInitializationException extends InitializationError{
    public PockitoInitializationException(List<Throwable> errors) {
        super(errors);
    }

    public PockitoInitializationException(Throwable error) {
        super(error);
    }

    public PockitoInitializationException(String string) {
        super(string);
    }
}
