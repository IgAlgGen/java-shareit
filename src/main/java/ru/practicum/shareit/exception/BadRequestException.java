package ru.practicum.shareit.exception;

/**
 * Исключение для ситуаций, когда запрос не проходит бизнес-валидацию.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
