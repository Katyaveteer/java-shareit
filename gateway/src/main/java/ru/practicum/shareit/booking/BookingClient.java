package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;


public class BookingClient extends BaseClient {

    private static final String BOOKINGS_PATH = "/bookings";

    public BookingClient(@Qualifier("gatewayRestTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }


    public ResponseEntity<Object> create(Long userId, BookingCreateDto dto) {
        return post(BOOKINGS_PATH, userId, dto);
    }


    public ResponseEntity<Object> approve(Long ownerId, Long bookingId, boolean approved) {
        return patch(BOOKINGS_PATH + "/" + bookingId + "?approved=" + approved, ownerId, null);
    }


    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get(BOOKINGS_PATH + "/" + bookingId, userId);
    }


    public ResponseEntity<Object> getUserBookings(Long userId, String state) {
        return get(BOOKINGS_PATH + "?state=" + state, userId);
    }


    public ResponseEntity<Object> getOwnerBookings(Long ownerId, String state) {
        return get(BOOKINGS_PATH + "/owner?state=" + state, ownerId);
    }


}
