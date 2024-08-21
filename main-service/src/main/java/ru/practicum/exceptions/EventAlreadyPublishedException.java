package ru.practicum.exceptions;

public class EventAlreadyPublishedException extends Exception {

    String message;

    public EventAlreadyPublishedException(String message) {
        super(message);
    }
}
