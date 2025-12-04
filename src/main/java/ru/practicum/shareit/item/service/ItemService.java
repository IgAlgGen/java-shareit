package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

/**
 * Сервис вещей.
 */
public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);

    ItemWithBookingsDto getById(Long requesterId, Long itemId);

    List<ItemWithBookingsDto> getOwnerItems(Long ownerId);

    List<ItemWithBookingsDto> search(String text);

    void deleteById(Long itemId);

    void deleteAllByOwnerId(Long ownerId);

    CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto);
}