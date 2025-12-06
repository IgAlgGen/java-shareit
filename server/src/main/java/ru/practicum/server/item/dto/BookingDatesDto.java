package ru.practicum.server.item.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Даты бронирования вещи.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDatesDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
}
