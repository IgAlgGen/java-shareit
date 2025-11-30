package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.BookingDatesDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
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
    private final BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper itemMapper,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
        this.bookingRepository = bookingRepository;
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

        Item saved = itemRepository.save(item);
        return itemMapper.toDto(saved);
    }

    @Override
    public ItemDetailsDto getById(Long requesterId, Long itemId) {
        ensureUserExists(requesterId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));
        return toDetailsDto(item);
    }

    @Override
    public List<ItemWithBookingsDto> getOwnerItems(Long ownerId) {
        ensureUserExists(ownerId);
        return itemRepository.findAllByOwnerIdOrderById(ownerId).stream()
                .map(this::toItemWithBookings)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDetailsDto> search(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(this::toDetailsDto)
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

    private ItemDetailsDto toDetailsDto(Item item) {
        return new ItemDetailsDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequestId());
    }

    private ItemWithBookingsDto toItemWithBookings(Item item) {
        List<Booking> bookings = bookingRepository.findByItem_Id(item.getId(), Sort.by("start"));
        LocalDateTime now = LocalDateTime.now();

        BookingDatesDto lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(now))
                .max(Comparator.comparing(Booking::getStart))
                .map(this::toBookingDatesDto)
                .orElse(null);

        BookingDatesDto nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .map(this::toBookingDatesDto)
                .orElse(null);

        return new ItemWithBookingsDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequestId(), lastBooking, nextBooking);
    }

    private BookingDatesDto toBookingDatesDto(Booking booking) {
        return new BookingDatesDto(booking.getId(), booking.getStart(), booking.getEnd());
    }
}