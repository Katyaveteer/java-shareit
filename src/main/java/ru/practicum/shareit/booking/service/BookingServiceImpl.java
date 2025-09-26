package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getAvailable()) {
            throw new RuntimeException("Item is not available for booking");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Owner cannot book their own item");
        }

        Booking booking = BookingMapper.toEntity(dto, item, user);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Только владелец может подтверждать бронирование");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new RuntimeException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Доступ запрещен");
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();
        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "CURRENT" ->
                    bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, sort);
            case "PAST" -> bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, sort);
            case "FUTURE" -> bookings = bookingRepository.findByBooker_Id(userId, sort).stream()
                    .filter(b -> b.getStart().isAfter(now)).toList();
            case "WAITING" -> bookings = bookingRepository.findByBooker_Id(userId, sort).stream()
                    .filter(b -> b.getStatus() == BookingStatus.WAITING).toList();
            case "REJECTED" -> bookings = bookingRepository.findByBooker_Id(userId, sort).stream()
                    .filter(b -> b.getStatus() == BookingStatus.REJECTED).toList();
            default -> bookings = bookingRepository.findByBooker_Id(userId, sort);
        }
        return bookings.stream().map(BookingMapper::toDto).toList();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId, sort);

        return bookings.stream().filter(b -> switch (state.toUpperCase()) {
            case "CURRENT" -> b.getStart().isBefore(now) && b.getEnd().isAfter(now);
            case "PAST" -> b.getEnd().isBefore(now);
            case "FUTURE" -> b.getStart().isAfter(now);
            case "WAITING" -> b.getStatus() == BookingStatus.WAITING;
            case "REJECTED" -> b.getStatus() == BookingStatus.REJECTED;
            default -> true;
        }).map(BookingMapper::toDto).toList();
    }
}
