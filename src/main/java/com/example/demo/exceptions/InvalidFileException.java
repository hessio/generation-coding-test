package com.example.demo.exceptions;

public class InvalidFileException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }
}