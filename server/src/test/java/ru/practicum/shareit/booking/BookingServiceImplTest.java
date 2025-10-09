package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl service;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        owner = User.builder().id(1L).name("Owner").email("o@mail.com").build();
        booker = User.builder().id(2L).name("Booker").email("b@mail.com").build();
        item = Item.builder().id(10L).name("Hammer").available(true).owner(owner).build();
        booking = Booking.builder()
                .id(100L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
    }


    @Test
    void create_shouldThrowIfDatesInvalid() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1)) // конец раньше начала
                .itemId(item.getId())
                .build();

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> service.create(booker.getId(), dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Дата окончания должна быть позже даты начала");

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void create_shouldSaveBooking() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = service.create(booker.getId(), dto);

        assertThat(result.getItem().getId()).isEqualTo(item.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        verify(bookingRepository).save(any());
    }


    @Test
    void approve_shouldChangeStatusToApproved() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = service.approve(owner.getId(), 100L, true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approve_shouldChangeStatusToRejected() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = service.approve(owner.getId(), 100L, false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approve_shouldThrowNotFound_whenBookingMissing() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.approve(owner.getId(), 999L, true))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void approve_shouldThrowForbidden_whenUserNotOwner() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThatThrownBy(() -> service.approve(999L, booking.getId(), true))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void approve_shouldThrowBadRequest_whenAlreadyProcessed() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.approve(owner.getId(), booking.getId(), true))
                .isInstanceOf(BadRequestException.class);
    }


    @Test
    void getById_shouldThrowIfNotAllowed() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.getById(999L, 100L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getUserBookings_shouldReturnList() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByBooker_Id(eq(booker.getId()), any(Sort.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> list = service.getUserBookings(booker.getId(), "ALL");

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getId()).isEqualTo(booking.getId());
        verify(bookingRepository).findByBooker_Id(eq(booker.getId()), any());
    }
}
