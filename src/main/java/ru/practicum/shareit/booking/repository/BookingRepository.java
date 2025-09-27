package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    // Все бронирования пользователя
    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    // Завершённые
    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    // Текущие
    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                               LocalDateTime start,
                                                               LocalDateTime end,
                                                               Sort sort);

    // Будущие
    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    // По статусу
    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);


    // Все бронирования для всех вещей владельца
    @Query("select b from Booking b where b.item.owner.id = ?1")
    List<Booking> findByOwnerId(Long ownerId, Sort sort);

    // Завершённые
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2")
    List<Booking> findPastBookingsForOwner(Long ownerId, LocalDateTime now, Sort sort);

    // Текущие
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findCurrentBookingsForOwner(Long ownerId, LocalDateTime now, Sort sort);

    // Будущие
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2")
    List<Booking> findFutureBookingsForOwner(Long ownerId, LocalDateTime now, Sort sort);

    // По статусу
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    Optional<Booking> findFirstByItem_IdAndStartBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Optional<Booking> findFirstByItem_IdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    boolean existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
            Long bookerId,
            Long itemId,
            BookingStatus status,
            LocalDateTime end);
}

