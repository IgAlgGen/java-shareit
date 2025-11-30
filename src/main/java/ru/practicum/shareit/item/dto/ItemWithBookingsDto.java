package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
