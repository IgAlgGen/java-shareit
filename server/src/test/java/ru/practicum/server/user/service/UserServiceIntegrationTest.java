package ru.practicum.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.user.dto.UserDto;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void create_shouldPersistUser() {
        UserDto created = userService.create(new UserDto(null, "Алиса", "alice@example.com"));

        assertThat(created.getId()).isNotNull();
        assertThat(userService.getAll())
                .extracting(UserDto::getEmail)
                .containsExactly("alice@example.com");
    }

    @Test
    void update_shouldChangePersistedFields() {
        UserDto created = userService.create(new UserDto(null, "Алиса", "alice@example.com"));

        UserDto updated = userService.update(created.getId(), new UserDto(null, "Кот", "cat@example.com"));

        assertThat(updated.getName()).isEqualTo("Кот");
        assertThat(userService.getById(created.getId()).getEmail()).isEqualTo("cat@example.com");
    }

    @Test
    void getById_shouldReturnPersistedUser() {
        UserDto created = userService.create(new UserDto(null, "Алиса", "alice@example.com"));

        UserDto found = userService.getById(created.getId());

        assertThat(found).usingRecursiveComparison().isEqualTo(created);
    }

    @Test
    void getAll_shouldReturnAllStoredUsers() {
        userService.create(new UserDto(null, "Алиса", "alice@example.com"));
        userService.create(new UserDto(null, "Кот", "cat@example.com"));

        List<UserDto> all = userService.getAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void delete_shouldRemoveUser() {
        UserDto created = userService.create(new UserDto(null, "Алиса", "alice@example.com"));

        userService.delete(created.getId());

        assertThrows(NotFoundException.class, () -> userService.getById(created.getId()));
    }
}
