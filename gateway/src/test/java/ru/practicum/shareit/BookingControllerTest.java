package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient client;

    private BookingDto createTestBookingDto() {
        UserDto booker = UserDto.builder()
                .id(1L)
                .name("Booker Name")
                .email("booker@example.com")
                .build();

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        return BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

    private BookingShortDto createTestBookingShortDto() {
        return BookingShortDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .bookerId(1L)
                .build();
    }

    @Test
    void shouldCreateBooking() throws Exception {
        BookingCreateDto createDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();

        BookingDto responseDto = createTestBookingDto();

        when(client.create(anyLong(), any(BookingCreateDto.class)))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldApproveBooking() throws Exception {
        BookingDto responseDto = createTestBookingDto();
        responseDto.setStatus(BookingStatus.APPROVED);

        when(client.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "2")
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetBookingById() throws Exception {
        BookingDto responseDto = createTestBookingDto();

        when(client.getById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUserBookings() throws Exception {
        BookingDto bookingDto = createTestBookingDto();
        List<BookingDto> bookings = List.of(bookingDto);

        when(client.getUserBookings(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(bookings));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetOwnerBookings() throws Exception {
        BookingDto bookingDto = createTestBookingDto();
        List<BookingDto> bookings = List.of(bookingDto);

        when(client.getOwnerBookings(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(bookings));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "CURRENT"))
                .andExpect(status().isOk());
    }


    @Test
    void shouldReturnBadRequestWhenCreateWithNullDates() throws Exception {
        BookingCreateDto invalidDto = BookingCreateDto.builder()
                .start(null) // null start date
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}