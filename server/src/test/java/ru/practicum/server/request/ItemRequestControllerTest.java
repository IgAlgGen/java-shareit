package ru.practicum.server.request;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.service.ItemRequestService;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void create_shouldReturnCreatedRequest() throws Exception {
        ItemRequestDto response = new ItemRequestDto(1L, "Нужна дрель", null, List.of());
        when(itemRequestService.create(eq(1L), any(ItemRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemRequestDto(null, "Нужна дрель", null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getOwn_shouldReturnUserRequests() throws Exception {
        when(itemRequestService.getOwn(1L)).thenReturn(List.of(new ItemRequestDto(1L, "Нужна дрель", null, List.of())));

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllOthers_shouldReturnOtherRequests() throws Exception {
        when(itemRequestService.getAllOthers(1L)).thenReturn(List.of());

        mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestService).getAllOthers(1L);
    }

    @Test
    void getById_shouldReturnRequest() throws Exception {
        when(itemRequestService.getById(1L, 2L)).thenReturn(new ItemRequestDto(2L, "Нужна дрель", null, List.of()));

        mockMvc.perform(get("/requests/2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }
}
