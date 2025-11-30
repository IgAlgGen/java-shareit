package ru.practicum.shareit.booking;

import java.util.List;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

/**
 * REST-контроллер бронирований.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader(userIdHeader) Long userId, @Valid @RequestBody BookingDto bookingDto) {
        log.info("Создание бронирования пользователем {}: {}", userId, bookingDto);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader(userIdHeader) Long ownerId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        log.info("Обновление статуса бронирования {} владельцем {} на {}", bookingId, ownerId, approved);
        return bookingService.updateStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(userIdHeader) Long userId, @PathVariable Long bookingId) {
        log.info("Получение бронирования {} пользователем {}", bookingId, userId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(userIdHeader) Long userId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение бронирований пользователя {} со статусом {}", userId, state);
        return bookingService.getBookerBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(userIdHeader) Long ownerId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение бронирований владельца {} со статусом {}", ownerId, state);
        return bookingService.getOwnerBookings(ownerId, state);
    }
}
