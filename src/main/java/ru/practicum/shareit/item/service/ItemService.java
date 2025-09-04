package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto dto, Long ownerId);

    ItemDto update(Long itemId, ItemDto partial, Long ownerId);

    ItemDto get(Long itemId, Long requesterId);

    List<ItemDto> getOwnerItems(Long ownerId);

    List<ItemDto> search(String text);
}
