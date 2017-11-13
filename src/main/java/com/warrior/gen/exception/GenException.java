package com.warrior.gen.exception;

public class GenException extends RuntimeException {

    public GenException() {
    }

    public GenException(String message) {
        super(message);
    }

    public GenException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenException(Throwable cause) {
        super(cause);
    }

    public GenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
