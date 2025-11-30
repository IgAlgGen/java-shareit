package ru.practicum.shareit.exception;

/**
 * Исключение для ситуаций, когда сущность не найдена или доступ к ней отсутствует.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
