package ru.practicum.shareit.item;

import java.util.List;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

/**
 * REST-контроллер для вещей.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    private final String ownerIdHeader = "X-Sharer-User-Id";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader(ownerIdHeader) Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Создание вещи: {}", itemDto);
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(ownerIdHeader) Long ownerId, @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Обновление вещи с ID {}: {}", itemId, itemDto);
        return itemService.update(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDetailsDto getById(@RequestHeader(ownerIdHeader) Long requesterId, @PathVariable Long itemId) {
        log.info("Получение вещи с ID {}", itemId);
        return itemService.getById(requesterId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getOwnerItems(@RequestHeader(ownerIdHeader) Long ownerId) {
        log.info("Получение списка вещей владельца с ID {}", ownerId);
        return itemService.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDetailsDto> search(@RequestParam String text) {
        log.info("Получение списка вещей по тексту: {}", text);
        return itemService.search(text);
    }
}