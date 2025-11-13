package ru.practicum.shareit.user.repository.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    private UserServiceImpl userService;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void create_shouldPersistUserWhenEmailIsUnique() {
        UserDto input = new UserDto(null, "Иван", "ivan@example.com");
        when(userRepository.existingUserEmail(any(User.class))).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User argument = invocation.getArgument(0);
            argument.setId(1L);
            return argument;
        });

        UserDto result = userService.create(input);

        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getName());
        assertEquals("ivan@example.com", result.getEmail());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).existingUserEmail(any(User.class));
        verify(userRepository).save(captor.capture());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void create_shouldThrowConflictWhenEmailExists() {
        when(userRepository.existingUserEmail(any(User.class))).thenReturn(true);
        UserDto input = new UserDto(null, "Иван", "ivan@example.com");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.create(input));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userRepository).existingUserEmail(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void update_shouldPatchExistingUser() {
        Long userId = 10L;
        UserDto patch = new UserDto(userId, "Другое Имя", "new@example.com");
        User stored = new User(userId, "Иван", "ivan@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(stored));
        when(userRepository.existingUserEmail(any(User.class))).thenReturn(false);
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.update(userId, patch);

        assertEquals(userId, result.getId());
        assertEquals("Другое Имя", result.getName());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository).findById(userId);
        verify(userRepository).existingUserEmail(any(User.class));
        verify(userRepository).update(any(User.class));
    }

    @Test
    void update_shouldThrowConflictWhenEmailExists() {
        Long userId = 1L;
        User stored = new User(userId, "Иван", "ivan@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(stored));
        when(userRepository.existingUserEmail(any(User.class))).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.update(userId, new UserDto(userId, "Роман", "roman@example.com")));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void getById_shouldThrowNotFoundWhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.getById(99L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getById_shouldReturnMappedDto() {
        User entity = new User(5L, "Иван", "ivan@example.com");
        when(userRepository.findById(5L)).thenReturn(Optional.of(entity));

        UserDto result = userService.getById(5L);

        assertEquals(userMapper.toDto(entity), result);
    }

    @Test
    void getAll_shouldReturnMappedList() {
        List<User> users = List.of(
                new User(1L, "Иван", "ivan@example.com"),
                new User(2L, "Роман", "roman@example.com")
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(userMapper.toDto(users.get(0))));
        assertTrue(result.contains(userMapper.toDto(users.get(1))));
    }

    @Test
    void delete_shouldRemoveExistingUser() {
        Long userId = 7L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "Иван", "ivan@example.com")));

        userService.delete(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }
}