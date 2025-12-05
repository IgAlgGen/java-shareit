package ru.practicum.shareit.request;

import java.util.List;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

/**
 * REST-контроллер запросов вещей.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    private final String userIdHeader = "X-Sharer-User-Id";

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader(userIdHeader) Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Пользователь {} создаёт запрос вещи: {}", userId, itemRequestDto.getDescription());
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwn(@RequestHeader(userIdHeader) Long userId) {
        log.info("Получение списка запросов пользователя {}", userId);
        return itemRequestService.getOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllOthers(@RequestHeader(userIdHeader) Long userId) {
        log.info("Получение запросов других пользователей для пользователя {}", userId);
        return itemRequestService.getAllOthers(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(userIdHeader) Long userId, @PathVariable Long requestId) {
        log.info("Получение запроса {} пользователем {}", requestId, userId);
        return itemRequestService.getById(userId, requestId);
    }
}
