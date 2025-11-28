package ru.practicum.shareit.user.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.validation.Valid;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

/**
 * Репозиторий пользователей в оперативке.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users;
    private final AtomicLong sequence;

    public InMemoryUserRepository() {
        this.users = new HashMap<>();
        this.sequence = new AtomicLong(1);
    }

    @Override
    public User save(@Valid User user) {
        if (user.getId() == null) {
            user.setId(sequence.incrementAndGet());
        }

        users.put(user.getId(), new User(user.getId(), user.getName(), user.getEmail()));
        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), new User(user.getId(), user.getName(), user.getEmail()));
        return users.get(user.getId());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    /**
     * Проверка на наличие записи с email в БД.
     * @param user пользователь
     * @return наличие email
     */
    @Override
    public boolean existingUserEmail(User user) {
        return users.values().stream()
                .anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()));

    }
}
