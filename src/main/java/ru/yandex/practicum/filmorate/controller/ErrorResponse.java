package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ErrorResponse - класс, представляющий ошибку в формате JSON:
 * Содержит поля для кода ошибки и ее описания.
 */
@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String error;
    private final String description;
}
