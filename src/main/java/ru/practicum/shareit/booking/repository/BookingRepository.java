package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

/**
 * Репозиторий бронирований.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start,
                                                          LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1")
    List<Booking> findByOwnerId(Long ownerId, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start <= ?2 and b.end >= ?3")
    List<Booking> findCurrentByOwnerId(Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.end < ?2")
    List<Booking> findPastByOwnerId(Long ownerId, LocalDateTime end, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start > ?2")
    List<Booking> findFutureByOwnerId(Long ownerId, LocalDateTime start, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.status = ?2")
    List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);
}
