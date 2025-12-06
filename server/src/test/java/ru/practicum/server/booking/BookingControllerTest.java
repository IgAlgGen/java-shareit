package ru.practicum.server.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.service.BookingService;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    private BookingController controller;

    @BeforeEach
    void setUp() {
        controller = new BookingController(bookingService);
    }

    @Test
    void create_shouldDelegateToService() {
        BookingDto request = new BookingDto(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), 3L, null, null, null, null);
        BookingDto response = new BookingDto(1L, request.getStart(), request.getEnd(), 3L, 5L,
                BookingStatus.WAITING, null, null);
        when(bookingService.create(5L, request)).thenReturn(response);

        BookingDto result = controller.create(5L, request);

        assertEquals(response, result);
        verify(bookingService).create(5L, request);
    }

    @Test
    void updateStatus_shouldPassParametersToService() {
        BookingDto response = new BookingDto(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                7L, 4L, BookingStatus.APPROVED, null, null);
        when(bookingService.updateStatus(9L, 2L, true)).thenReturn(response);

        BookingDto result = controller.updateStatus(9L, 2L, true);

        assertEquals(response, result);
        verify(bookingService).updateStatus(9L, 2L, true);
    }

    @Test
    void getById_shouldReturnServiceResult() {
        BookingDto response = new BookingDto(3L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                7L, 4L, BookingStatus.APPROVED, null, null);
        when(bookingService.getById(4L, 3L)).thenReturn(response);

        BookingDto result = controller.getById(4L, 3L);

        assertEquals(response, result);
        verify(bookingService).getById(4L, 3L);
    }

    @Test
    void getUserBookings_shouldReturnServiceResult() {
        List<BookingDto> bookings = List.of(new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                7L, 4L, BookingStatus.APPROVED, null, null));
        when(bookingService.getBookerBookings(4L, "ALL")).thenReturn(bookings);

        List<BookingDto> result = controller.getUserBookings(4L, "ALL");

        assertEquals(bookings, result);
        verify(bookingService).getBookerBookings(4L, "ALL");
    }

    @Test
    void getOwnerBookings_shouldReturnServiceResult() {
        List<BookingDto> bookings = List.of(new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                7L, 4L, BookingStatus.APPROVED, null, null));
        when(bookingService.getOwnerBookings(9L, "FUTURE")).thenReturn(bookings);

        List<BookingDto> result = controller.getOwnerBookings(9L, "FUTURE");

        assertEquals(bookings, result);
        verify(bookingService).getOwnerBookings(9L, "FUTURE");
    }
}
