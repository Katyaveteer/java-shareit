package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


public final class ItemRequestMapper {
    private ItemRequestMapper() {
    }

    public static ItemRequestDto toDto(ItemRequest r) {
        if (r == null) return null;
        return ItemRequestDto.builder()
                .id(r.getId())
                .description(r.getDescription())
                .requestorId(r.getRequestorId() != null ? r.getRequestorId() : null)
                .build();
    }

    public static ItemRequest fromDto(ItemRequestDto d, Long requestorId) {
        if (d == null) return null;
        User requestor = User.builder().id(requestorId).build();
        return ItemRequest.builder()
                .id(d.getId())
                .description(d.getDescription())
                .requestorId(requestor.getId())
                .build();
    }
}
