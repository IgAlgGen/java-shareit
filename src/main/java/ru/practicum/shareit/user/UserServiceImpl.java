package ru.practicum.shareit.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * Реализация сервиса пользователей.
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (userRepository.existingUserEmail(user)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже существует в БД");
        }
        user.setId(null);
        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        final User existing = userExist(id);
        if (userRepository.existingUserEmail(UserMapper.toUser(userDto))){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже существует в БД");
        }
        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existing.setEmail(userDto.getEmail());
        }
        User saved = userRepository.update(existing);
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto getById(Long id) {
        final User user = userExist(id);
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        userExist(id);
        userRepository.deleteById(id);
    }

    private User userExist(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
    }
}
