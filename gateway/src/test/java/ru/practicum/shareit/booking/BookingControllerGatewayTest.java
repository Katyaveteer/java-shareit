package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerGatewayTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private BookingCreateDto validBooking;
    private BookingCreateDto invalidBooking;

    @BeforeEach
    void setUp() {
        validBooking = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        invalidBooking = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    void shouldReturn400_whenCreateBookingWithInvalidDates() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCallBookingClient_whenCreateBookingWithValidDates() throws Exception {
        when(bookingClient.bookItem(anyLong(), any(BookingCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBooking)))
                .andExpect(status().isOk());

        verify(bookingClient).bookItem(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    void shouldCallBookingClient_whenGetBookingById() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(anyLong(), anyLong());
    }

    @Test
    void shouldCallBookingClient_whenApproveBooking() throws Exception {
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/1/approve")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldCallBookingClient_whenGetBookingsList() throws Exception {

        when(bookingClient.getBookings(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk());


        verify(bookingClient).getBookings(anyLong(), any(BookingState.class));
    }
}
