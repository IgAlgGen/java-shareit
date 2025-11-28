package ru.practicum.shareit.item.repository;

import java.util.List;
import java.util.Optional;
import ru.practicum.shareit.item.model.Item;

/**
 * Репозиторий вещей.
 */
public interface ItemRepository {
    Item save(Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> search(String text);

    void deleteById(Long itemId);

    void deleteAllByOwnerId(Long ownerId);
}