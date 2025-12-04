package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO вещи для подробного просмотра.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemDetailsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private List<CommentDto> comments;
}
