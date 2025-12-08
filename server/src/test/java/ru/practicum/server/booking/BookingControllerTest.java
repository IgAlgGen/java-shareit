package ru.practicum.server.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.booking.model.BookingStatus;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void create_shouldReturnBooking() throws Exception {
        BookingDto request = new BookingDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L, null, null, null, null);
        when(bookingService.create(eq(1L), any(BookingDto.class))).thenReturn(new BookingDto(1L, request.getStart(), request.getEnd(), 1L, 1L, BookingStatus.WAITING, null, null));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateStatus_shouldReturnUpdatedBooking() throws Exception {
        when(bookingService.updateStatus(1L, 2L, true)).thenReturn(new BookingDto(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 1L, BookingStatus.APPROVED, null, null));

        mockMvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    void getById_shouldReturnBooking() throws Exception {
        when(bookingService.getById(1L, 2L)).thenReturn(new BookingDto(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 1L, BookingStatus.APPROVED, null, null));

        mockMvc.perform(get("/bookings/2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void getUserBookings_shouldReturnList() throws Exception {
        when(bookingService.getBookerBookings(1L, "ALL")).thenReturn(List.of());

        mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingService).getBookerBookings(1L, "ALL");
    }

    @Test
    void getOwnerBookings_shouldReturnList() throws Exception {
        when(bookingService.getOwnerBookings(1L, "ALL")).thenReturn(List.of());

        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingService).getOwnerBookings(1L, "ALL");
    }
}
