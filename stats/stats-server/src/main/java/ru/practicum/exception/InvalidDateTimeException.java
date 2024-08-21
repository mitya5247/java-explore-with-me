package ru.practicum.exception;

public class InvalidDateTimeException extends Exception {

    String message;

    public InvalidDateTimeException(String message) {
        super(message);
    }
}
