package ru.practicum.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO вещи с данными о бронированиях.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingDatesDto lastBooking;
    private BookingDatesDto nextBooking;
    private List<CommentDto> comments;
}
