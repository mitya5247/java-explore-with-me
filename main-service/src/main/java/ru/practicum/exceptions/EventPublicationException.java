package ru.practicum.exceptions;

public class EventPublicationException extends Exception {

    String message;

    public EventPublicationException(String message) {
        super(message);
    }
}
