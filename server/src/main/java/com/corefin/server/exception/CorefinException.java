package com.corefin.server.exception;

public class CorefinException extends RuntimeException {
    public CorefinException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
