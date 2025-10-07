package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortGatewayDto;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemWithBookingsDto {
    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус доступности обязателен")
    private Boolean available;

    private BookingShortGatewayDto lastBooking;
    private BookingShortGatewayDto nextBooking;
    private List<CommentDto> comments;
}
