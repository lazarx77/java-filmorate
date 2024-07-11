package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.Map;

/**
 * ErrorHandler.
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.error("Ошибка валидации данных: {}.", e.getMessage());
        return Map.of(
                "error", "Ошибка валидации данных",
                "description", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.error("Ошибка с входными параметрами: {}.", e.getMessage());
        return Map.of(
                "error", "Ошибка с входными параметрами.",
                "description", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnexpectedException(final Throwable e) {
        if (e instanceof MethodArgumentNotValidException) {
            return handleValidationException((ValidationException) e);
        }
        log.error("Возникло исключение: {}.", e.getMessage());
        return Map.of(
                "error", "Возникло исключение",
                "description", e.getMessage()
        );
    }
}
