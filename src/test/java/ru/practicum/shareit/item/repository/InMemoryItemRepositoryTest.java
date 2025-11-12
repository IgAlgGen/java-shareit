package ru.practicum.shareit.item.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

class InMemoryItemRepositoryTest {
    private InMemoryItemRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryItemRepository();
    }

    @Test
    void save_shouldAssignIdentifier() {
        Item item = new Item(null, "Дрель", "Сильная", true, 1L, null);

        Item saved = repository.save(item);

        assertNotNull(saved.getId());
        assertEquals(saved, repository.findById(saved.getId()).orElseThrow());
    }

    @Test
    void findByOwnerId_shouldReturnSortedItems() {
        Item first = repository.save(new Item(null, "Первый", "Первый", true, 1L, null));
        Item second = repository.save(new Item(null, "Второй", "Второй", true, 1L, null));
        repository.save(new Item(null, "Другой", "Другой", true, 2L, null));

        List<Item> items = repository.findByOwnerId(1L);

        assertEquals(2, items.size());
        assertTrue(items.get(0).getId() < items.get(1).getId());
        assertEquals(first.getOwnerId(), items.get(0).getOwnerId());
        assertEquals(second.getOwnerId(), items.get(1).getOwnerId());
    }

    @Test
    void search_shouldReturnMatchesWhenAvailable() {
        Item matching = repository.save(new Item(null, "Дрель", "Сильная", true, 1L, null));
        repository.save(new Item(null, "Молоток", "Тяжелый", false, 1L, null));
        repository.save(new Item(null, "Пила", "Пилящая", true, 1L, null));

        List<Item> result = repository.search("Дрель");

        assertEquals(1, result.size());
        assertEquals(matching, result.get(0));
    }

    @Test
    void search_shouldReturnEmptyForBlankText() {
        repository.save(new Item(null, "Дрель", "Сильная", true, 1L, null));

        assertTrue(repository.search(" ").isEmpty());
    }

    @Test
    void deleteById_shouldRemoveItem() {
        Item saved = repository.save(new Item(null, "Дрель", "Сильная", true, 1L, null));

        repository.deleteById(saved.getId());

        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void deleteAllByOwnerId_shouldRemoveOwnersItems() {
        Item owned = repository.save(new Item(null, "Дрель", "Сильная", true, 1L, null));
        repository.save(new Item(null, "Пила", "Пилящая", true, 2L, null));

        repository.deleteAllByOwnerId(1L);

        assertTrue(repository.findById(owned.getId()).isEmpty());
        assertFalse(repository.findByOwnerId(2L).isEmpty());
    }
}