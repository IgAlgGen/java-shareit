package ru.practicum.server.request.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    private Long requestorId;
    private Long otherUserId;

    @BeforeEach
    void setUp() {
        requestorId = userService.create(new UserDto(null, "Запросчик", "requester@example.com")).getId();
        otherUserId = userService.create(new UserDto(null, "Юзер", "user@example.com")).getId();
    }

    @Test
    void create_shouldPersistRequest() {
        ItemRequestDto created = itemRequestService.create(requestorId, new ItemRequestDto(null, "Нужна дрель", null, null));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void getOwn_shouldReturnOnlyRequestorRequests() {
        ItemRequestDto created = itemRequestService.create(requestorId, new ItemRequestDto(null, "Нужна дрель", null, null));

        List<ItemRequestDto> own = itemRequestService.getOwn(requestorId);

        assertThat(own).extracting(ItemRequestDto::getId).containsExactly(created.getId());
    }

    @Test
    void getAllOthers_shouldReturnRequestsFromOthers() {
        ItemRequestDto created = itemRequestService.create(requestorId, new ItemRequestDto(null, "Нужна дрель", null, null));

        List<ItemRequestDto> others = itemRequestService.getAllOthers(otherUserId);

        assertThat(others).extracting(ItemRequestDto::getId).containsExactly(created.getId());
    }

    @Test
    void getById_shouldRequireExistingUser() {
        ItemRequestDto created = itemRequestService.create(requestorId, new ItemRequestDto(null, "Нужна дрель", null, null));

        ItemRequestDto found = itemRequestService.getById(requestorId, created.getId());
        assertThat(found.getId()).isEqualTo(created.getId());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(999L, created.getId()));
    }
}
