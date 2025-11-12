package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

/**
 * Маппер пользователя.
 */
public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}