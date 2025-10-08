package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreateDto {
    @NotNull(message = "Дата начала не может быть нулевой")
    @FutureOrPresent(message = "дата начала должна быть в настоящем или будущем")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания не может быть нулевой")
    @Future(message = "Дата окончания должна быть в будущем.")
    private LocalDateTime end;
    @NotNull(message = "Идентификатор вещи не может быть нулевым")
    private Long itemId;
}