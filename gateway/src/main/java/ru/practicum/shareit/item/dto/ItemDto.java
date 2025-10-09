package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Builder
public class ItemDto {


    private String name;


    private String description;

    private Boolean available;

    private Long requestId;
    @Builder.Default
    private List<CommentDto> comments = List.of();
}
