package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Builder
public class ItemDto {

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус доступности обязателен")
    private Boolean available;

    private Long requestId;
    @Builder.Default
    private List<CommentDto> comments = List.of();
}
