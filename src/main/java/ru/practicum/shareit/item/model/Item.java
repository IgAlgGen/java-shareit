package ru.practicum.shareit.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

/**
 * Вещь, доступная для шеринга.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "Статус доступности не может быть пустым")
    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "request_id")
    private Long requestId;
}
