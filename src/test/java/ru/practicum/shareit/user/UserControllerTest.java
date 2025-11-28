package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController(userService);
    }

    @Test
    void create_shouldDelegateToService() {
        UserDto request = new UserDto(null, "Alice", "alice@example.com");
        UserDto response = new UserDto(1L, "Alice", "alice@example.com");
        when(userService.create(any(UserDto.class))).thenReturn(response);

        UserDto result = controller.create(request);

        assertEquals(response, result);
        verify(userService).create(request);
    }

    @Test
    void update_shouldDelegateToService() {
        UserDto patch = new UserDto(null, "Alice", "alice@example.com");
        UserDto response = new UserDto(1L, "Alice", "alice@example.com");
        when(userService.update(1L, patch)).thenReturn(response);

        UserDto result = controller.update(1L, patch);

        assertEquals(response, result);
        verify(userService).update(1L, patch);
    }

    @Test
    void getById_shouldReturnServiceResult() {
        UserDto response = new UserDto(2L, "Bob", "bob@example.com");
        when(userService.getById(2L)).thenReturn(response);

        UserDto result = controller.getById(2L);

        assertEquals(response, result);
        verify(userService).getById(2L);
    }

    @Test
    void getAll_shouldReturnServiceResult() {
        List<UserDto> users = List.of(new UserDto(1L, "Alice", "alice@example.com"));
        when(userService.getAll()).thenReturn(users);

        List<UserDto> result = controller.getAll();

        assertEquals(users, result);
        verify(userService).getAll();
    }

    @Test
    void delete_shouldInvokeService() {
        controller.delete(5L);

        verify(userService).delete(5L);
    }
}
