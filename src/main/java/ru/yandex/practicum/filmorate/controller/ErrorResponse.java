package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ErrorResponse.
 */
@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String error;
    private final String description;
}
