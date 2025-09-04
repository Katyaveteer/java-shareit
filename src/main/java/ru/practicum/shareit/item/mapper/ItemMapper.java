package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public final class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto toDto(Item i) {
        if (i == null) return null;
        return ItemDto.builder()
                .id(i.getId())
                .name(i.getName())
                .description(i.getDescription())
                .available(i.getAvailable())
                .requestId(i.getRequest() != null ? i.getRequest().getId() : null)
                .build();
    }

    public static Item fromDto(ItemDto d, Long ownerId) {
        if (d == null) return null;
        User owner = User.builder().id(ownerId).build(); // owner only id for now
        ItemRequest req = d.getRequestId() != null ? ItemRequest.builder().id(d.getRequestId()).build() : null;
        return Item.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .available(d.getAvailable())
                .owner(owner)
                .request(req)
                .build();
    }
}
