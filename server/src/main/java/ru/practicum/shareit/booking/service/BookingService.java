

package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;


import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingCreateDto dto);

    BookingDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, BookingState state);

    List<BookingDto> getOwnerBookings(Long ownerId, BookingState state);

    BookingShortDto getLastBooking(Long itemId);

    BookingShortDto getNextBooking(Long itemId);

    Booking getBookingWithUserBookedItem(Long itemId, Long userId);

}






