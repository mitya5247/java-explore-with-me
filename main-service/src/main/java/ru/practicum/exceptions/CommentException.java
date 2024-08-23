package ru.practicum.exceptions;

public class CommentException extends Exception {

    String message;

    public CommentException(String message) {
        super(message);
    }
}
