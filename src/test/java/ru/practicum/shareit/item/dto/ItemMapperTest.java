package ru.practicum.shareit.item.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;

class ItemMapperTest {
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void toDto_shouldConvertEntity() {
        Item item = new Item(1L, "Дрель", "Сильная", true, 2L, 3L);

        ItemDto dto = itemMapper.toDto(item);

        assertEquals(1L, dto.getId());
        assertEquals("Дрель", dto.getName());
        assertEquals("Сильная", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(3L, dto.getRequestId());
    }

    @Test
    void toDto_shouldReturnNullForNullInput() {
        assertNull(itemMapper.toDto(null));
    }

    @Test
    void toItem_shouldConvertDto() {
        ItemDto dto = new ItemDto(1L, "Дрель", "Сильная", true, 3L);

        Item item = itemMapper.toItem(dto);

        assertEquals(1L, item.getId());
        assertEquals("Дрель", item.getName());
        assertEquals("Сильная", item.getDescription());
        assertTrue(item.getAvailable());
        assertNull(item.getOwnerId());
        assertEquals(3L, item.getRequestId());
    }

    @Test
    void toItem_shouldReturnNullForNullInput() {
        assertNull(itemMapper.toItem(null));
    }
}
