package com.prueba.exception;

public class InvalidCardStateException extends RuntimeException {
    public InvalidCardStateException(String message) {
        super(message);
    }
}
