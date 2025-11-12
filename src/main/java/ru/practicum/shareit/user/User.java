package ru.practicum.shareit.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Пользователь сервиса ShareIt.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class User {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @NotNull
    @NotEmpty(message = "Email не может быть пустым")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть корректным")
    private String email;
}