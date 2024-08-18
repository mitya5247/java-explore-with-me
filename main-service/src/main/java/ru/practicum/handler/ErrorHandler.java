package ru.practicum.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exceptions.*;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final EmailAlreadyExistsException e) {
        List<StackTraceElement> list = List.of(e.getStackTrace());
        log.info("email already exists");
        return new ApiError(list, e.getMessage(), e.getCause().toString(), HttpStatus.CONFLICT.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final RequestErrorException e) {
        List<StackTraceElement> list = List.of(e.getStackTrace());
        log.info("error request exception");
        return new ApiError(list, e.getMessage(), e.getCause().toString(), HttpStatus.CONFLICT.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final EntityNotFoundException e) {
        List<StackTraceElement> list = List.of(e.getStackTrace());
        log.info("entity not found");
        ApiError apiError = new ApiError();
        apiError.setErrors(list);
        if (e.getCause() != null) {
            apiError.setReason(e.getCause().toString());
        }
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.NOT_FOUND.toString());
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConsraintViolationException(final ConstraintViolationException e) {
        List<StackTraceElement> list = List.of(e.getStackTrace());
        log.info("bad request");
        ApiError apiError = new ApiError();
        apiError.setErrors(list);
        if (e.getCause() != null) {
            apiError.setReason(e.getCause().toString());
        }
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.BAD_REQUEST.toString());
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlePatchException(final EventPatchException e) {
        List<StackTraceElement> list = List.of(e.getStackTrace());
        log.info("bad request");
        ApiError apiError = new ApiError();
        apiError.setErrors(list);
        if (e.getCause() != null) {
            apiError.setReason(e.getCause().toString());
        }
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.BAD_REQUEST.toString());
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlePatchException(final SQLException e) {
        List<StackTraceElement> list = List.of(e.getStackTrace());
        log.info("duplicate");
        ApiError apiError = new ApiError();
        apiError.setErrors(list);
        if (e.getCause() != null) {
            apiError.setReason(e.getCause().toString());
        }
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.BAD_REQUEST.toString());
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlePatchException(final EventAlreadyPublishedException e) {
        List<StackTraceElement> list = List.of(e.getStackTrace());
        log.info("duplicate");
        ApiError apiError = new ApiError();
        apiError.setErrors(list);
        if (e.getCause() != null) {
            apiError.setReason(e.getCause().toString());
        }
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.BAD_REQUEST.toString());
        return apiError;
    }
}
