package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDto;

/**
 * Сервис бронирований.
 */
public interface BookingService {
    BookingDto create(Long bookerId, BookingDto bookingDto);

    BookingDto updateStatus(Long ownerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getBookerBookings(Long bookerId, String state);

    List<BookingDto> getOwnerBookings(Long ownerId, String state);
}
