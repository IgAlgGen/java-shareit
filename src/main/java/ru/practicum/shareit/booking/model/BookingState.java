package ru.practicum.shareit.booking.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown state: " + value);
        }
    }
}
