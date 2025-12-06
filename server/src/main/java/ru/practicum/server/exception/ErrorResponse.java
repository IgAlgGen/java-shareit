package ru.practicum.server.exception;

import lombok.Getter;

/**
 * Класс для формирования ответа с ошибкой.
 */
@Getter
public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

}
