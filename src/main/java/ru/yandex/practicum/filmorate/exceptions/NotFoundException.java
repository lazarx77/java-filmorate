package ru.yandex.practicum.filmorate.exceptions;

/**
 * NotFoundException.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
