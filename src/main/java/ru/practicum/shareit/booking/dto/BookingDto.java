package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * DTO бронирования.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDto {
    private Long id;

    @NotNull(message = "Дата начала бронирования не может быть пустой")
    @Future(message = "Дата начала бронирования должна быть в будущем")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть пустой")
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;

    @NotNull(message = "Идентификатор вещи не может быть пустым")
    private Long itemId;

    private Long bookerId;

    private BookingStatus status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ItemDto item;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto booker;
}
