package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;

/**
 * Сервис вещей.
 */
public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);

    ItemDto getById(Long requesterId, Long itemId);

    List<ItemDto> getOwnerItems(Long ownerId);

    List<ItemDto> search(String text);

    void deleteById(Long itemId);

    void deleteAllByOwnerId(Long ownerId);
}