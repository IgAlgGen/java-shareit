package ru.practicum.server.booking.model;

import ru.practicum.server.exception.BadRequestException;

/**
 * Возможные состояния выборки бронирований.
 */
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String value) {
        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Неизвестное состояние бронирования: " + value);
        }
    }
}
