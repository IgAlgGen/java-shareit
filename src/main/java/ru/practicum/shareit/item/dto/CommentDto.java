package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO комментария к вещи.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
