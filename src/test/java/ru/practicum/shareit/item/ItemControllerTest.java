package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;

    private ItemController controller;

    @BeforeEach
    void setUp() {
        controller = new ItemController(itemService);
    }

    @Test
    void create_shouldDelegateToService() {
        ItemDto request = new ItemDto(null, "Drill", "Powerful", true, null);
        ItemDto response = new ItemDto(1L, "Drill", "Powerful", true, null);
        when(itemService.create(1L, request)).thenReturn(response);

        ItemDto result = controller.create(1L, request);

        assertEquals(response, result);
        verify(itemService).create(1L, request);
    }

    @Test
    void update_shouldDelegateToService() {
        ItemDto request = new ItemDto(null, "Дрель", "Сильная", true, null);
        ItemDto response = new ItemDto(1L, "Дрель", "Сильная", false, null);
        when(itemService.update(1L, 2L, request)).thenReturn(response);

        ItemDto result = controller.update(1L, 2L, request);

        assertEquals(response, result);
        verify(itemService).update(1L, 2L, request);
    }

    @Test
    void getById_shouldReturnServiceResult() {
        ItemDto response = new ItemDto(2L, "Дрель", "Сильная", true, null);
        when(itemService.getById(1L, 2L)).thenReturn(response);

        ItemDto result = controller.getById(1L, 2L);

        assertEquals(response, result);
        verify(itemService).getById(1L, 2L);
    }

    @Test
    void getOwnerItems_shouldReturnServiceResult() {
        List<ItemDto> items = List.of(new ItemDto(1L, "Дрель", "Сильная", true, null));
        when(itemService.getOwnerItems(3L)).thenReturn(items);

        List<ItemDto> result = controller.getOwnerItems(3L);

        assertEquals(items, result);
        verify(itemService).getOwnerItems(3L);
    }

    @Test
    void search_shouldReturnServiceResult() {
        List<ItemDto> items = List.of(new ItemDto(1L, "Дрель", "Сильная", true, null));
        when(itemService.search("text")).thenReturn(items);

        List<ItemDto> result = controller.search("text");

        assertEquals(items, result);
        verify(itemService).search("text");
    }
}
