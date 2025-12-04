package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.BookingDatesDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
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
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper itemMapper,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
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
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!ownerId.equals(item.getOwnerId())) {
            throw new NotFoundException("Вещь не принадлежит пользователю");
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
    public ItemWithBookingsDto getById(Long requesterId, Long itemId) {
        ensureUserExists(requesterId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<CommentDto> comments = getCommentsForItem(item.getId());
        if (item.getOwnerId().equals(requesterId)) {
            return toItemWithBookings(item, comments);
        }
        return new ItemWithBookingsDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequestId(), null, null, comments);
    }

    @Override
    public List<ItemWithBookingsDto> getOwnerItems(Long ownerId) {
        ensureUserExists(ownerId);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(ownerId);
        Map<Long, List<CommentDto>> comments = getCommentsForItems(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        return items.stream()
                .map(item -> toItemWithBookings(item, comments.getOrDefault(item.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemWithBookingsDto> search(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        List<Item> items = itemRepository.search(text);
        Map<Long, List<CommentDto>> comments = getCommentsForItems(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        return items.stream()
                .map(item -> toItemWithBookings(item, comments.getOrDefault(item.getId(), List.of())))
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

    @Override
    public CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        LocalDateTime now = LocalDateTime.now();
        boolean hadBooking = bookingRepository.existsByBooker_IdAndItem_IdAndEndIsBeforeAndStatus(authorId, itemId,
                now, BookingStatus.APPROVED);
        if (!hadBooking) {
            throw new BadRequestException(
                    "Только арендаторы после окончания бронирования могут оставлять комментарии");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        Comment saved = commentRepository.save(comment);
        return toCommentDto(saved);
    }

    private void ensureUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private ItemDetailsDto toDetailsDto(Item item) {
        List<CommentDto> comments = getCommentsForItem(item.getId());
        return toDetailsDto(item, comments);
    }

    private ItemDetailsDto toDetailsDto(Item item, List<CommentDto> comments) {
        return new ItemDetailsDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequestId(), comments);
    }

    private ItemWithBookingsDto toItemWithBookings(Item item, List<CommentDto> comments) {
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
                item.getRequestId(), lastBooking, nextBooking, comments);
    }

    private BookingDatesDto toBookingDatesDto(Booking booking) {
        return new BookingDatesDto(booking.getId(), booking.getStart(), booking.getEnd());
    }

    private List<CommentDto> getCommentsForItem(Long itemId) {
        return commentRepository.findByItem_IdOrderByCreatedAsc(itemId).stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    private Map<Long, List<CommentDto>> getCommentsForItems(List<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<CommentDto>> commentsByItem = new HashMap<>();
        commentRepository.findByItem_IdInOrderByCreatedAsc(itemIds)
                .forEach(comment -> commentsByItem
                        .computeIfAbsent(comment.getItem().getId(), key -> new java.util.ArrayList<>())
                        .add(toCommentDto(comment)));
        return commentsByItem;
    }

    private CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}