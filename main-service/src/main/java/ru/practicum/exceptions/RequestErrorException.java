package ru.practicum.exceptions;

public class RequestErrorException extends Exception {

    String message;

    public RequestErrorException(String message) {
        super(message);
    }
}
