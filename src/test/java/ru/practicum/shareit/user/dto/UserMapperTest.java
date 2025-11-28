package ru.practicum.shareit.user.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.model.User;

class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDto_shouldConvertEntity() {
        User user = new User(1L, "Иван", "ivan@example.com");

        UserDto dto = mapper.toDto(user);

        assertEquals(1L, dto.getId());
        assertEquals("Иван", dto.getName());
        assertEquals("ivan@example.com", dto.getEmail());
    }

    @Test
    void toDto_shouldReturnNullForNullInput() {
        assertNull(mapper.toDto(null));
    }

    @Test
    void toUser_shouldConvertDto() {
        UserDto dto = new UserDto(1L, "Иван", "ivan@example.com");

        User user = mapper.toUser(dto);

        assertEquals(1L, user.getId());
        assertEquals("Иван", user.getName());
        assertEquals("ivan@example.com", user.getEmail());
    }

    @Test
    void toUser_shouldReturnNullForNullInput() {
        assertNull(mapper.toUser(null));
    }
}