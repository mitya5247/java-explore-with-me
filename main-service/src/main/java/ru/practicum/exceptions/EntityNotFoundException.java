package ru.practicum.exceptions;

public class EntityNotFoundException extends Exception {
    String message;

    public EntityNotFoundException(String message) {
        super(message);
    }
}
