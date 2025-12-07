package ru.practicum.gateway.item;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.item.dto.ItemUpdateDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(
            @Value("${shareit-server.url:http://localhost:9090}") String serverUrl,
            RestTemplateBuilder builder
    ) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(long ownerId, Long itemId, ItemUpdateDto itemDto) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> getById(long requesterId, Long itemId) {
        return get("/" + itemId, requesterId);
    }

    public ResponseEntity<Object> getOwnerItems(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(String text) {
        Map<String, Object> params = Map.of("text", text);
        return get("/search?text={text}", null, params);
    }

    public ResponseEntity<Object> addComment(long authorId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", authorId, commentDto);
    }
}
