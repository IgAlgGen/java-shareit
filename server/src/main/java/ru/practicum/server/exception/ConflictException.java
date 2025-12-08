package ru.practicum.server.exception;

/**
 * Исключение для конфликтных ситуаций (дублирование данных и т.д.).
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
