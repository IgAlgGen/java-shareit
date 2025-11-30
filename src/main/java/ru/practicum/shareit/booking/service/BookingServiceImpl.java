package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

/**
 * Реализация сервиса бронирований.
 */
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository,
                              UserRepository userRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingDto create(Long bookerId, BookingDto bookingDto) {
        validateDates(bookingDto);
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));
        if (bookerId.equals(item.getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Владелец не может бронировать свою вещь");
        }
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вещь недоступна для бронирования");
        }

        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setId(null);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    @Override
    public BookingDto updateStatus(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирование не найдено"));
        if (!ownerId.equals(booking.getItem().getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Только владелец может изменять статус");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Статус уже установлен");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Нет доступа к бронированию");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookerBookings(Long bookerId, String state) {
        ensureUserExists(bookerId);
        BookingState bookingState = BookingState.from(state);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;
        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(bookerId, now, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(bookerId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(bookerId, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED, sort);
                break;
            default:
                bookings = bookingRepository.findByBooker_Id(bookerId, sort);
        }
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        ensureUserExists(ownerId);
        BookingState bookingState = BookingState.from(state);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;
        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwnerId(ownerId, now, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findPastByOwnerId(ownerId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByOwnerId(ownerId, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, sort);
                break;
            default:
                bookings = bookingRepository.findByOwnerId(ownerId, sort);
        }
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateDates(BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Даты бронирования обязательны");
        }
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата окончания должна быть позже даты начала");
        }
    }

    private void ensureUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
    }
}
