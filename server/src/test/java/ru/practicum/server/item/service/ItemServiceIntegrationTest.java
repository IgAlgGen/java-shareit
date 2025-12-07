package ru.practicum.server.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.exception.BadRequestException;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemWithBookingsDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.repository.UserRepository;
import ru.practicum.server.user.service.UserService;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Long ownerId;
    private Long bookerId;

    @BeforeEach
    void setUp() {
        ownerId = userService.create(new UserDto(null, "Хозяин", "owner@example.com")).getId();
        bookerId = userService.create(new UserDto(null, "Реквестор", "booker@example.com")).getId();
    }

    @Test
    void create_shouldPersistItemWithOwner() {
        ItemDto created = itemService.create(ownerId, new ItemDto(null, "Дрель", "Сильная", true, null));

        assertThat(created.getId()).isNotNull();
        Item stored = itemRepository.findById(created.getId()).orElseThrow();
        assertThat(stored.getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    void update_shouldModifyStoredItem() {
        ItemDto created = itemService.create(ownerId, new ItemDto(null, "Дрель", "Сильная", true, null));

        ItemDto updated = itemService.update(ownerId, created.getId(), new ItemDto(null, "Молоток", null, null, null));

        assertThat(updated.getName()).isEqualTo("Молоток");
        assertThat(itemRepository.findById(created.getId()).orElseThrow().getName()).isEqualTo("Молоток");
    }

    @Test
    void getById_shouldReturnDetailsWithBookingsForOwner() {
        ItemDto created = itemService.create(ownerId, new ItemDto(null, "Дрель", "Сильная", true, null));
        Item item = itemRepository.findById(created.getId()).orElseThrow();

        Booking booking = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, userRepository.findById(bookerId).orElseThrow(), BookingStatus.APPROVED);
        bookingRepository.save(booking);

        ItemWithBookingsDto result = itemService.getById(ownerId, item.getId());

        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getLastBooking().getId()).isEqualTo(booking.getId());
    }

    @Test
    void getOwnerItems_shouldReturnAllWithComments() {
        ItemDto created = itemService.create(ownerId, new ItemDto(null, "Дрель", "Сильная", true, null));
        Item item = itemRepository.findById(created.getId()).orElseThrow();

        Booking booking = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, userRepository.findById(bookerId).orElseThrow(), BookingStatus.APPROVED);
        bookingRepository.save(booking);
        bookingRepository.flush();

        CommentDto commentDto = itemService.addComment(bookerId, item.getId(), new CommentDto(null, "отлично", null, null));

        List<ItemWithBookingsDto> items = itemService.getOwnerItems(ownerId);

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getComments()).extracting(CommentDto::getText).containsExactly(commentDto.getText());
    }

    @Test
    void search_shouldFindAvailableItemsByText() {
        itemService.create(ownerId, new ItemDto(null, "Дрель", "Сильная", true, null));
        itemService.create(ownerId, new ItemDto(null, "Молоток", "Крепкий", false, null));

        List<ItemWithBookingsDto> results = itemService.search("Дрель");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void addComment_shouldFailWithoutApprovedBookingInPast() {
        ItemDto created = itemService.create(ownerId, new ItemDto(null, "Дрель", "Сильная", true, null));

        assertThrows(BadRequestException.class,
                () -> itemService.addComment(bookerId, created.getId(), new CommentDto(null, "отлично", null, null)));
    }
}
