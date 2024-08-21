package ru.practicum.exceptions;

public class EventPatchException extends Exception {
    String message;

    public EventPatchException(String message) {
        super(message);
    }
}
