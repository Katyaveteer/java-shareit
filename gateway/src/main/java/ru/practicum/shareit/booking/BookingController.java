package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.LocalDateTime;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookings(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingCreateDto requestDto) {

        LocalDateTime start = requestDto.getStart();
        LocalDateTime end = requestDto.getEnd();


        if (start == null || end == null) {
            throw new BadRequestException("Даты начала и окончания не должны быть нулевыми.");
        }


        if (!end.isAfter(start)) {
            throw new BadRequestException("Дата окончания должна быть после даты начала.");
        }


        if (start.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала не должна быть в прошлом.");
        }

        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}/approve")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }


}

