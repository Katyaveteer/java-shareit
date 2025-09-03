package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    // skeleton: real implementation in next sprint
    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody BookingDto dto) {
        throw new UnsupportedOperationException("Booking endpoints will be implemented in next sprint");
    }
}
