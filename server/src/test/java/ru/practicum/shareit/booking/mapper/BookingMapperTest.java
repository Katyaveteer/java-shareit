package ru.practicum.shareit.booking.mapper;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingMapperTest {

    private final User user = User.builder().id(1L).name("Alice").email("a@mail.com").build();
    private final Item item = Item.builder().id(2L).name("Drill").description("Power tool").available(true).owner(user).build();

    @Test
    void toEntity_shouldMapCorrectly() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();

        Booking booking = BookingMapper.toEntity(dto, item, user);

        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(user);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void toDto_shouldMapAllFields() {
        Booking booking = Booking.builder()
                .id(10L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();

        BookingDto dto = BookingMapper.toDto(booking);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getBooker().getId()).isEqualTo(user.getId());
        assertThat(dto.getItem().getId()).isEqualTo(item.getId());
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void toShortDto_shouldMapMinimalFields() {
        Booking booking = Booking.builder()
                .id(100L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .booker(user)
                .item(item)
                .build();

        BookingShortDto dto = BookingMapper.toShortDto(booking);

        assertThat(dto.getBookerId()).isEqualTo(user.getId());
        assertThat(dto.getId()).isEqualTo(100L);
    }
}

