package ru.practicum.exceptions;

public class EventIsNotPublishedException extends Exception {
    String message;

    public EventIsNotPublishedException(String message) {
        super(message);
    }
}
