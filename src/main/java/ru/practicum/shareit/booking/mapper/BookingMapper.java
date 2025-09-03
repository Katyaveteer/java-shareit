package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public final class BookingMapper {
    private BookingMapper() {
    }

    public static BookingDto toDto(Booking b) {
        if (b == null) return null;
        return BookingDto.builder()
                .id(b.getId())
                .start(b.getStart())
                .end(b.getEnd())
                .itemId(b.getItem() != null ? b.getItem().getId() : null)
                .bookerId(b.getBooker() != null ? b.getBooker().getId() : null)
                .status(b.getStatus() != null ? b.getStatus().name() : null)
                .build();
    }
}
