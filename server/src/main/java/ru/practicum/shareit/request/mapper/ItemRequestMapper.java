package ru.practicum.shareit.request.mapper;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;


public final class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestDto toDto(ItemRequest request) {
        if (request == null) {
            return null;
        }

        User requester = request.getRequester();
        UserDto requesterDto = null;
        if (requester != null) {
            requesterDto = UserDto.builder()
                    .id(requester.getId())
                    .name(requester.getName())
                    .email(requester.getEmail())
                    .build();
        }

        List<ItemDto> items = null;
        if (request.getItems() != null) {
            items = request.getItems().stream()
                    .map(item -> ItemDto.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .description(item.getDescription())
                            .available(item.getAvailable())
                            .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                            .build())
                    .collect(Collectors.toList());
        }

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(requesterDto)
                .created(request.getCreated())
                .items(items)
                .build();
    }


    public static ItemRequest fromCreateDto(ItemRequestCreateDto dto, User requester) {
        if (dto == null) {
            return null;
        }

        return ItemRequest.builder()
                .description(dto.getDescription())
                .requester(requester)
                .created(java.time.LocalDateTime.now())
                .build();
    }
}


