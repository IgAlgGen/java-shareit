package ru.practicum.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO вещи в ответе на запрос.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestItemDto {
    private Long id;
    private String name;
    private Long ownerId;
}
