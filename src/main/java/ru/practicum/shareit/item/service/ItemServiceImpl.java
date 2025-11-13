package ru.practicum.shareit.item.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

/**
 * Реализация сервиса вещей.
 */
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        ensureUserExists(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(ownerId);
        Item saved = itemRepository.save(item);
        return itemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        ensureUserExists(ownerId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));
        if (!ownerId.equals(item.getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не принадлежит пользователю");
        }
        String name = Objects.nonNull(itemDto.getName()) ? itemDto.getName() : item.getName();
        String description = Objects.nonNull(itemDto.getDescription()) ? itemDto.getDescription() : item.getDescription();
        Boolean available = Objects.nonNull(itemDto.getAvailable()) ? itemDto.getAvailable() : item.getAvailable();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);

        Item saved = itemRepository.update(item);
        return itemMapper.toDto(saved);
    }

    @Override
    public ItemDto getById(Long requesterId, Long itemId) {
        ensureUserExists(requesterId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        ensureUserExists(ownerId);
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public void deleteAllByOwnerId(Long ownerId) {
        itemRepository.deleteAllByOwnerId(ownerId);
    }

    private void ensureUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
    }
}