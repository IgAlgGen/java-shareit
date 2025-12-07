package ru.practicum.gateway.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.item.dto.ItemUpdateDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Создание вещи пользователем {}: {}", ownerId, itemDto);
        return itemClient.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                         @PathVariable Long itemId,
                                         @RequestBody ItemUpdateDto itemDto) {
        log.info("Обновление вещи {} пользователем {}: {}", itemId, ownerId, itemDto);
        return itemClient.update(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                          @PathVariable Long itemId) {
        log.info("Получение вещи {} пользователем {}", itemId, requesterId);
        return itemClient.getById(requesterId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получение вещей владельца {}", ownerId);
        return itemClient.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Поиск вещей по тексту: {}", text);
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok().body(java.util.List.of());
        }
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария к вещи {} пользователем {}", itemId, authorId);
        return itemClient.addComment(authorId, itemId, commentDto);
    }
}
