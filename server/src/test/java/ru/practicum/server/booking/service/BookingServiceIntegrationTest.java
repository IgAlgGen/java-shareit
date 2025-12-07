package ru.practicum.server.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        ownerId = userService.create(new UserDto(null, "Хозяин", "owner@example.com")).getId();
        bookerId = userService.create(new UserDto(null, "Реквестор", "booker@example.com")).getId();
        itemId = itemService.create(ownerId, new ItemDto(null, "Дрель", "Сильная", true, null)).getId();
    }

    @Test
    void create_shouldPersistWaitingBooking() {
        BookingDto booking = new BookingDto(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), itemId, null, null, null, null);

        BookingDto created = bookingService.create(bookerId, booking);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void updateStatus_shouldChangeWaitingStatus() {
        BookingDto booking = bookingService.create(bookerId, new BookingDto(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), itemId, null, null, null, null));

        BookingDto updated = bookingService.updateStatus(ownerId, booking.getId(), true);

        assertThat(updated.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getById_shouldReturnForBookerAndOwner() {
        BookingDto booking = bookingService.create(bookerId, new BookingDto(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), itemId, null, null, null, null));

        assertThat(bookingService.getById(bookerId, booking.getId()).getId()).isEqualTo(booking.getId());
        assertThat(bookingService.getById(ownerId, booking.getId()).getId()).isEqualTo(booking.getId());
        assertThrows(NotFoundException.class, () -> bookingService.getById(999L, booking.getId()));
    }

    @Test
    void getBookerBookings_shouldFilterByState() {
        BookingDto past = bookingService.create(bookerId, new BookingDto(null, LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2), itemId, null, BookingStatus.APPROVED, null, null));
        bookingService.updateStatus(ownerId, past.getId(), true);

        List<BookingDto> all = bookingService.getBookerBookings(bookerId, "ALL");

        assertThat(all).extracting(BookingDto::getId).contains(past.getId());
    }

    @Test
    void getOwnerBookings_shouldReturnOwnerRelated() {
        BookingDto booking = bookingService.create(bookerId, new BookingDto(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), itemId, null, null, null, null));

        List<BookingDto> ownerBookings = bookingService.getOwnerBookings(ownerId, "ALL");

        assertThat(ownerBookings).extracting(BookingDto::getId).contains(booking.getId());
    }

    @Test
    void create_shouldFailWhenOwnerTriesToBookOwnItem() {
        BookingDto booking = new BookingDto(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), itemId, null, null, null, null);

        assertThrows(NotFoundException.class, () -> bookingService.create(ownerId, booking));
    }
}
