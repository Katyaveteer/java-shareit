package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
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
    void shouldPerformCompleteBookingFlow() {
        // === 1. Подготовка данных ===
        UserDto owner = userService.create(new UserDto(null, "Owner", "owner@example.com"));
        UserDto booker = userService.create(new UserDto(null, "Booker", "booker@example.com"));

        ItemDto item1 = itemService.create(owner.getId(), ItemDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .build());

        ItemDto item2 = itemService.create(owner.getId(), ItemDto.builder()
                .name("Saw")
                .description("Circular saw")
                .available(true)
                .build());


        BookingCreateDto futureBookingDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item1.getId())
                .build();

        BookingDto futureBooking = bookingService.create(booker.getId(), futureBookingDto);
        assertThat(futureBooking).isNotNull();
        assertThat(futureBooking.getStatus()).isEqualTo(BookingStatus.WAITING);

        BookingCreateDto secondBookingDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .itemId(item2.getId())
                .build();

        BookingDto secondBooking = bookingService.create(booker.getId(), secondBookingDto);
        assertThat(secondBooking).isNotNull();


        BookingDto approvedBooking = bookingService.approve(owner.getId(), futureBooking.getId(), true);
        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);

        BookingDto rejectedBooking = bookingService.approve(owner.getId(), secondBooking.getId(), false);
        assertThat(rejectedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);


        BookingDto foundByBooker = bookingService.getById(booker.getId(), futureBooking.getId());
        assertThat(foundByBooker.getId()).isEqualTo(futureBooking.getId());

        BookingDto foundByOwner = bookingService.getById(owner.getId(), futureBooking.getId());
        assertThat(foundByOwner.getId()).isEqualTo(futureBooking.getId());


        List<BookingDto> allBookings = bookingService.getUserBookings(booker.getId(), BookingState.ALL);
        assertThat(allBookings).hasSize(2);

        List<BookingDto> futureBookings = bookingService.getUserBookings(booker.getId(), BookingState.FUTURE);
        assertThat(futureBookings).hasSize(2);

        List<BookingDto> waitingBookings = bookingService.getUserBookings(booker.getId(), BookingState.WAITING);
        assertThat(waitingBookings).isEmpty();

        List<BookingDto> rejectedBookings = bookingService.getUserBookings(booker.getId(), BookingState.REJECTED);
        assertThat(rejectedBookings).hasSize(1);
        assertThat(rejectedBookings.getFirst().getStatus()).isEqualTo(BookingStatus.REJECTED);


        List<BookingDto> ownerAll = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);
        assertThat(ownerAll).hasSize(2);

        long approvedCount = ownerAll.stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .count();
        long rejectedCount = ownerAll.stream()
                .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                .count();

        assertThat(approvedCount).isEqualTo(1);
        assertThat(rejectedCount).isEqualTo(1);


        try {
            bookingService.approve(owner.getId(), futureBooking.getId(), true);
            assertThat(false).isTrue();
        } catch (ConflictException e) {
            assertThat(e.getMessage()).contains("Бронирование уже обработано");
        }

        try {
            BookingCreateDto ownItemBookingDto = BookingCreateDto.builder()
                    .start(LocalDateTime.now().plusDays(5))
                    .end(LocalDateTime.now().plusDays(6))
                    .itemId(item1.getId())
                    .build();
            bookingService.create(owner.getId(), ownItemBookingDto);
            assertThat(false).isTrue();
        } catch (ForbiddenException e) {
            assertThat(e.getMessage()).contains("Владелец не может забронировать свой собственный товар");
        }
    }
}
