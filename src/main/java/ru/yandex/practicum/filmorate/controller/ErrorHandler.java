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
 * ErrorHandler - класс для обработки исключений и формирования соответствующих ответов.
 * Он перехватывает различные типы исключений, такие как ValidationException, NotFoundException и другие,
 * и возвращает соответствующие ответы с описанием ошибки и кодом состояния HTTP.
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    /**
     * handleValidationException - обрабатывает исключения типа ValidationException и MethodArgumentNotValidException.
     * Возвращает ответ с кодом состояния HTTP 400 (BAD_REQUEST) и описанием ошибки валидации данных.
     *
     * @param e исключение типа ValidationException
     * @return Map с описанием ошибки валидации данных
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.error("Ошибка валидации данных: {}.", e.getMessage());
        return Map.of(
                "error", "Ошибка валидации данных",
                "description", e.getMessage()
        );
    }

    /**
     * handleNotFoundException - обрабатывает исключения типа NotFoundException.
     * Возвращает ответ с кодом состояния HTTP 404 (NOT_FOUND) и описанием ошибки с входными параметрами.
     *
     * @param e исключение типа NotFoundException
     * @return Map с описанием ошибки с входными параметрами
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.error("Ошибка с входными параметрами: {}.", e.getMessage());
        return Map.of(
                "error", "Ошибка с входными параметрами.",
                "description", e.getMessage()
        );
    }

    /**
     * handleThrowable - обрабатывает любые другие исключения типа Throwable.
     * Возвращает ответ с кодом состояния HTTP 500 (INTERNAL_SERVER_ERROR) и описанием возникшей ошибки.
     *
     * @param e исключение типа Throwable
     * @return Map с описанием возникшей ошибки
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable e) {
        log.error("Возникла ошибка: {}.", e.getMessage());
        return Map.of(
                "error", "Возникла ошибка.",
                "description", e.getMessage()
        );
    }
}
