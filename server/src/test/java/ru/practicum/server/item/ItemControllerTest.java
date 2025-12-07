package ru.practicum.server.item;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemWithBookingsDto;
import ru.practicum.server.item.service.ItemService;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void create_shouldDelegateToService() throws Exception {
        ItemDto request = new ItemDto(null, "Drill", "Powerful", true, null);
        when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(new ItemDto(1L, "Drill", "Powerful", true, null));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_shouldReturnUpdatedItem() throws Exception {
        when(itemService.update(eq(1L), eq(2L), any(ItemDto.class)))
                .thenReturn(new ItemDto(2L, "Updated", "Powerful", true, null));

        mockMvc.perform(patch("/items/2")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void getById_shouldReturnItem() throws Exception {
        when(itemService.getById(1L, 2L)).thenReturn(new ItemWithBookingsDto(2L, "Drill", "Powerful", true, null, null, null, List.of()));

        mockMvc.perform(get("/items/2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void getOwnerItems_shouldReturnList() throws Exception {
        when(itemService.getOwnerItems(1L)).thenReturn(List.of(new ItemWithBookingsDto(1L, "Drill", "Powerful", true, null, null, null, List.of())));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void search_shouldCallService() throws Exception {
        when(itemService.search("текст")).thenReturn(List.of());

        mockMvc.perform(get("/items/search").param("text", "текст"))
                .andExpect(status().isOk());

        verify(itemService).search("текст");
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        CommentDto response = new CommentDto(1L, "текст", "Иван", null);
        when(itemService.addComment(eq(1L), eq(2L), any(CommentDto.class))).thenReturn(response);

        mockMvc.perform(post("/items/2/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentDto(null, "текст", null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
