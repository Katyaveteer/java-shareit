package ru.practicum.shareit.integration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void shouldCreateAndApproveBooking() {

        UserDto owner = userService.create(new UserDto(null, "Owner", "owner@example.com"));
        UserDto booker = userService.create(new UserDto(null, "Booker", "booker@example.com"));


        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .build();
        ItemDto item = itemService.create(owner.getId(), itemDto);


        BookingCreateDto createDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();

        BookingDto booking = bookingService.create(booker.getId(), createDto);
        assertThat(booking).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);

        // Подтверждаем бронирование
        BookingDto approved = bookingService.approve(owner.getId(), booking.getId(), true);
        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void shouldGetBookingsByState() {

        UserDto owner = userService.create(new UserDto(null, "Owner", "owner@example.com"));
        UserDto booker = userService.create(new UserDto(null, "Booker", "booker@example.com"));
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Drill").description("Drill").available(true).build());

        BookingCreateDto createDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();

        bookingService.create(booker.getId(), createDto);

        List<BookingDto> bookings = bookingService.getUserBookings(booker.getId(), "ALL");
        assertThat(bookings).hasSize(1);
    }
}
