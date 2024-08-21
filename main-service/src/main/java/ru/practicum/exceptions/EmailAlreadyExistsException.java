package ru.practicum.exceptions;

public class EmailAlreadyExistsException extends Exception {
    String message;

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
