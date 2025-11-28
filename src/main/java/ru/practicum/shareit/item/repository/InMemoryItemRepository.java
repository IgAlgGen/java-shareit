package ru.practicum.shareit.item.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.item.model.Item;

/**
 * Репозиторий вещей в оперативке.
 */
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items;
    private final AtomicLong sequence;

    public InMemoryItemRepository() {
        this.items = new HashMap<>();
        this.sequence = new AtomicLong(1);
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(sequence.incrementAndGet());
        }
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findByOwnerId(Long ownerId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (ownerId.equals(item.getOwnerId())) {
                result.add(item);
            }
        }
        result.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return result;
    }

    @Override
    public List<Item> search(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        String lower = text.toLowerCase();
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (Boolean.TRUE.equals(item.getAvailable())) {
                String name = item.getName() == null ? "" : item.getName().toLowerCase();
                String description = item.getDescription() == null ? ""
                        : item.getDescription().toLowerCase();
                if (name.contains(lower) || description.contains(lower)) {
                    result.add(item);
                }
            }
        }
        result.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return result;
    }

    @Override
    public void deleteById(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public void deleteAllByOwnerId(Long ownerId) {
        items.values().removeIf(item -> ownerId.equals(item.getOwnerId()));
    }
}