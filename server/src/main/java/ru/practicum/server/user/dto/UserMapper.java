package ru.practicum.server.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import ru.practicum.server.user.model.User;

/**
 * Маппер пользователя.
 */
@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    UserDto toDto(User user);

    User toUser(UserDto userDto);
}