package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {

    private static final String BOOKINGS_PATH = "/bookings";

    public BookingClient(@Qualifier("gatewayRestTemplate") RestTemplate restTemplate,
                         @Value("${shareit.server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    // Создание бронирования
    public ResponseEntity<Object> create(Long userId, BookingCreateDto dto) {
        return post(BOOKINGS_PATH, userId, null, dto);
    }

    // Подтверждение/отмена бронирования
    public ResponseEntity<Object> approve(Long ownerId, Long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch(BOOKINGS_PATH + "/" + bookingId, ownerId, parameters, null);
    }

    // Получение бронирования по ID
    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get(BOOKINGS_PATH + "/" + bookingId, userId, null);
    }

    // Получение всех бронирований пользователя с фильтром по состоянию
    public ResponseEntity<Object> getUserBookings(Long userId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get(BOOKINGS_PATH, userId, parameters);
    }

    // Получение всех бронирований владельца с фильтром по состоянию
    public ResponseEntity<Object> getOwnerBookings(Long ownerId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get(BOOKINGS_PATH + "/owner", ownerId, parameters);
    }
}

