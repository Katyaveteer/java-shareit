package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;


import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    private BookingCreateDto validDto;

    @BeforeEach
    void setUp() {
        validDto = new BookingCreateDto();
        validDto.setItemId(1L);
        validDto.setStart(LocalDateTime.now().plusDays(1));
        validDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBooking_whenValidRequest_thenReturnsOk() throws Exception {
        Mockito.when(bookingClient.create(eq(1L), any()))
                .thenReturn(ResponseEntity.ok().build());

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2030, 1, 1, 10, 0));
        dto.setEnd(LocalDateTime.of(2030, 1, 2, 10, 0));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient).create(eq(1L), any());
    }

    @Test
    void createBooking_whenEndBeforeStart_thenBadRequest() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2030, 1, 2, 10, 0));
        dto.setEnd(LocalDateTime.of(2030, 1, 1, 10, 0));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void approveBooking_thenDelegatesToClient() throws Exception {
        Mockito.when(bookingClient.approve(1L, 2L, true))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient).approve(1L, 2L, true);
    }

    @Test
    void getById_thenDelegatesToClient() throws Exception {
        Mockito.when(bookingClient.getById(1L, 5L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/5")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient).getById(1L, 5L);
    }

    @Test
    void getUserBookings_thenDelegatesToClient() throws Exception {
        Mockito.when(bookingClient.getUserBookings(1L, BookingState.ALL.name()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient).getUserBookings(1L, "ALL");
    }

    @Test
    void getOwnerBookings_thenDelegatesToClient() throws Exception {
        Mockito.when(bookingClient.getOwnerBookings(1L, BookingState.ALL.name()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient).getOwnerBookings(1L, "ALL");
    }
}
