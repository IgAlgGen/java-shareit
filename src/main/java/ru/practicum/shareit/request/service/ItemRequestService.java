package ru.practicum.shareit.request.service;

import java.util.List;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * Сервис для работы с запросами вещей.
 */
public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getOwn(Long userId);

    List<ItemRequestDto> getAllOthers(Long userId);

    ItemRequestDto getById(Long userId, Long requestId);
}
