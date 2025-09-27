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
                .requestorId(r.getRequesterId() != null ? r.getRequesterId() : null)
                .build();
    }

    public static ItemRequest fromDto(ItemRequestDto d, Long requesterId) {
        if (d == null) return null;
        User requester = User.builder().id(requesterId).build();
        return ItemRequest.builder()
                .id(d.getId())
                .description(d.getDescription())
                .requesterId(requester.getId())
                .build();
    }
}
