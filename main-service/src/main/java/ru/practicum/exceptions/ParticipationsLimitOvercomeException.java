package ru.practicum.exceptions;

public class ParticipationsLimitOvercomeException extends Exception {

    String message;

    public ParticipationsLimitOvercomeException(String message) {
        super(message);
    }
}
