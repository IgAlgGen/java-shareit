package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер пользователя.
 */
@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    UserDto toDto(User user);

    User toUser(UserDto userDto);
}