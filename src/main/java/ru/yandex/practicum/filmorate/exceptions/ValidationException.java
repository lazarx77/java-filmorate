package ru.yandex.practicum.filmorate.exceptions;

/**
 * ValidationException.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
