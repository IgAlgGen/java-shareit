package ru.practicum.shareit.item.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    private ItemServiceImpl itemService;
    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = Mappers.getMapper(ItemMapper.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, itemMapper, bookingRepository,
                commentRepository);
    }

    @Test
    void create_shouldPersistItemWithOwner() {
        Long ownerId = 3L;
        ItemDto dto = new ItemDto(null, "Дрель", "Сильная", true, null);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User(ownerId, "Иван", "ivan@example.com")));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item argument = invocation.getArgument(0);
            argument.setId(5L);
            return argument;
        });

        ItemDto result = itemService.create(ownerId, dto);

        assertEquals(5L, result.getId());
        assertEquals("Дрель", result.getName());
        assertEquals("Сильная", result.getDescription());
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());
        assertEquals(ownerId, captor.getValue().getOwnerId());
    }

    @Test
    void create_shouldFailWhenOwnerMissing() {
        when(userRepository.findById(77L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> itemService.create(77L, new ItemDto(null, "Дрель", "Сильная", true, null)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_shouldMergeChangesWhenOwnerMatches() {
        Long ownerId = 2L;
        Long itemId = 9L;
        Item stored = new Item(itemId, "Прежнее", "Прежнее описание", true, ownerId, null);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User(ownerId, "Owner", "owner@example.com")));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(stored));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto patch = new ItemDto(null, "Новое", "Новое описание", false, null);
        ItemDto result = itemService.update(ownerId, itemId, patch);

        assertEquals(itemId, result.getId());
        assertEquals("Новое", result.getName());
        assertEquals("Новое описание", result.getDescription());
        assertFalse(result.getAvailable());
        verify(itemRepository).save(stored);
    }

    @Test
    void update_shouldThrowWhenOwnerDoesNotMatch() {
        Long ownerId = 1L;
        Item stored = new Item(4L, "Старое", "Старое описание", true, 999L, null);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User(ownerId, "Иван", "ivan@example.com")));
        when(itemRepository.findById(4L)).thenReturn(Optional.of(stored));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> itemService.update(ownerId, 4L, new ItemDto(null, "Новое", null, null, null)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void update_shouldThrowWhenItemMissing() {
        Long ownerId = 1L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User(ownerId, "Иван", "ivan@example.com")));
        when(itemRepository.findById(4L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> itemService.update(ownerId, 4L, new ItemDto(null, "Новое", null, null, null)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getById_shouldReturnDtoWhenEverythingExists() {
        Long requesterId = 1L;
        Long itemId = 3L;
        Item entity = new Item(itemId, "Вещь", "Описание", true, 1L, null);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(new User(requesterId, "Иван", "ivan@example.com")));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(entity));
        when(commentRepository.findByItem_IdOrderByCreatedAsc(itemId)).thenReturn(List.of());

        ItemWithBookingsDto result = itemService.getById(requesterId, itemId);

        assertEquals(new ItemWithBookingsDto(itemId, "Вещь", "Описание", true, null, null,null, List.of()), result);
    }

    @Test
    void getById_shouldThrowWhenRequesterMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> itemService.getById(1L, 2L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getOwnerItems_shouldReturnSortedDtos() {
        Long ownerId = 2L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User(ownerId, "Иван", "ivan@example.com")));
        List<Item> items = List.of(
                new Item(10L, "Вещь1", "Описание1", true, ownerId, null),
                new Item(11L, "Вещь2", "Описание2", true, ownerId, null)
        );
        when(itemRepository.findAllByOwnerIdOrderById(ownerId)).thenReturn(items);
        when(bookingRepository.findByItem_Id(any(Long.class), any(Sort.class))).thenReturn(List.of());
        when(commentRepository.findByItem_IdInOrderByCreatedAsc(any())).thenReturn(List.of());

        List<ItemWithBookingsDto> result = itemService.getOwnerItems(ownerId);

        assertEquals(2, result.size());
        assertEquals(new ItemWithBookingsDto(10L, "Вещь1", "Описание1", true, null, null, null, List.of()), result.get(0));
        assertEquals(new ItemWithBookingsDto(11L, "Вещь2", "Описание2", true, null, null, null, List.of()), result.get(1));
    }

    @Test
    void getOwnerItems_shouldFillLastAndNextBookings() {
        Long ownerId = 3L;
        Item item = new Item(5L, "Вещь", "Описание", true, ownerId, null);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User(ownerId, "Иван", "ivan@example.com")));
        when(itemRepository.findAllByOwnerIdOrderById(ownerId)).thenReturn(List.of(item));

        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = new Booking(1L, now.minusDays(3), now.minusDays(2), item, null, BookingStatus.APPROVED);
        Booking futureBooking = new Booking(2L, now.plusDays(1), now.plusDays(2), item, null, BookingStatus.APPROVED);
        when(bookingRepository.findByItem_Id(item.getId(), Sort.by("start")))
                .thenReturn(List.of(pastBooking, futureBooking));
        when(commentRepository.findByItem_IdInOrderByCreatedAsc(any())).thenReturn(List.of());

        List<ItemWithBookingsDto> result = itemService.getOwnerItems(ownerId);

        assertEquals(1, result.size());
        ItemWithBookingsDto dto = result.get(0);
        assertEquals(pastBooking.getId(), dto.getLastBooking().getId());
        assertEquals(pastBooking.getStart(), dto.getLastBooking().getStart());
        assertEquals(futureBooking.getId(), dto.getNextBooking().getId());
        assertEquals(futureBooking.getStart(), dto.getNextBooking().getStart());
    }

    @Test
    void search_shouldMapResultsFromRepository() {
        List<Item> items = List.of(
                new Item(1L, "Вещь1", "Описание1", true, 1L, null),
                new Item(2L, "Вещь2", "Описание2", true, 2L, null)
        );
        when(itemRepository.search("вещь")).thenReturn(items);
        when(commentRepository.findByItem_IdInOrderByCreatedAsc(any())).thenReturn(List.of());

        List<ItemWithBookingsDto> result = itemService.search("вещь");

        assertEquals(2, result.size());
        assertEquals(new ItemWithBookingsDto(1L, "Вещь1", "Описание1", true, null,null,null, List.of()), result.get(0));
        assertEquals(new ItemWithBookingsDto(2L, "Вещь2", "Описание2", true, null, null, null, List.of()), result.get(1));
    }

    @Test
    void deleteById_shouldDelegateToRepository() {
        itemService.deleteById(5L);

        verify(itemRepository).deleteById(5L);
    }

    @Test
    void deleteAllByOwnerId_shouldDelegateToRepository() {
        itemService.deleteAllByOwnerId(9L);

        verify(itemRepository).deleteAllByOwnerId(9L);
    }

    @Test
    void addComment_shouldSaveWhenBookingFinished() {
        Long authorId = 4L;
        Long itemId = 7L;
        User author = new User(authorId, "Иван", "ivan@example.com");
        Item item = new Item(itemId, "Вещь", "Описание", true, 3L, null);
        CommentDto request = new CommentDto(null, "Отлично", null, null);
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndEndIsBeforeAndStatus(eq(authorId), eq(itemId),
                any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CommentDto result = itemService.addComment(authorId, itemId, request);

        assertEquals("Отлично", result.getText());
        assertEquals("Иван", result.getAuthorName());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_shouldRejectWhenNoFinishedBookings() {
        Long authorId = 4L;
        Long itemId = 7L;
        User author = new User(authorId, "Иван", "ivan@example.com");
        Item item = new Item(itemId, "Вещь", "Описание", true, 3L, null);
        CommentDto request = new CommentDto(null, "Отлично", null, null);
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndEndIsBeforeAndStatus(any(), any(), any(), any()))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> itemService.addComment(authorId, itemId, request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(commentRepository, never()).save(any(Comment.class));
    }
}