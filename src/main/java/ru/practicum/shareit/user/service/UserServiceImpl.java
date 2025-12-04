package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

/**
 * Реализация сервиса пользователей.
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email уже существует в БД");
        }
        user.setId(null);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        final User existing = userExist(id);
        if (userDto.getEmail() != null && userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("Email уже существует в БД");
        }
        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existing.setEmail(userDto.getEmail());
        }
        User saved = userRepository.save(existing);
        return userMapper.toDto(saved);
    }

    @Override
    public UserDto getById(Long id) {
        final User user = userExist(id);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        userExist(id);
        userRepository.deleteById(id);
    }

    private User userExist(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
