package ru.practicum.exceptions;

public class ValidationException extends Exception {

    String message;

    public ValidationException(String message) {
        super(message);
    }
}
