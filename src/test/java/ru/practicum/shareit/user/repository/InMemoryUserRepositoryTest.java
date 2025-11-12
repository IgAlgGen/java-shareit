package ru.practicum.shareit.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void save_shouldAssignIdentifierAndStoreCopy() {
        User user = new User(null, "Иван", "ivan@example.com");

        User saved = repository.save(user);

        assertNotNull(saved.getId());
        assertEquals(saved, repository.findById(saved.getId()).orElseThrow());
        assertNotSame(user, saved);
    }

    @Test
    void update_shouldReplaceExistingEntry() {
        User user = repository.save(new User(null, "Иван", "ivan@example.com"));
        user.setName("Роман");
        user.setEmail("roman@example.com");

        User updated = repository.update(user);

        assertEquals("Роман", updated.getName());
        assertEquals("roman@example.com", updated.getEmail());
        assertEquals(updated, repository.findById(updated.getId()).orElseThrow());
    }

    @Test
    void findAll_shouldReturnAllStoredUsers() {
        repository.save(new User(null, "Иван", "ivan@example.com"));
        repository.save(new User(null, "Роман", "roman@example.com"));

        List<User> result = repository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void deleteById_shouldRemoveUser() {
        User saved = repository.save(new User(null, "Иван", "ivan@example.com"));

        repository.deleteById(saved.getId());

        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void existingUserEmail_shouldDetectDuplicates() {
        repository.save(new User(null, "Иван", "ivan@example.com"));

        boolean duplicate = repository.existingUserEmail(new User(null, "Иван", "ivan@example.com"));
        boolean unique = repository.existingUserEmail(new User(null, "Роман", "roman@example.com"));

        assertTrue(duplicate);
        assertFalse(unique);
    }
}