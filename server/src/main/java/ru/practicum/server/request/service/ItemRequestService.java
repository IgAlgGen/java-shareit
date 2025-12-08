package ru.practicum.server.request.service;

import java.util.List;
import ru.practicum.server.request.dto.ItemRequestDto;

/**
 * Сервис для работы с запросами вещей.
 */
public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getOwn(Long userId);

    List<ItemRequestDto> getAllOthers(Long userId);

    ItemRequestDto getById(Long userId, Long requestId);
}
