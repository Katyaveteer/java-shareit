package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemValidator;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository items;
    private final UserRepository users;

    @Override
    public ItemDto create(ItemDto dto, Long ownerId) {
        ItemValidator.validator(dto);
        users.findById(ownerId).orElseThrow(() -> new NotFoundException("Владелец не найден"));
        Item saved = items.save(ItemMapper.fromDto(dto, ownerId));
        return ItemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long itemId, ItemDto partial, Long ownerId) {
        Item existing = items.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (existing.getOwner() == null || !existing.getOwner().getId().equals(ownerId))
            throw new NotFoundException("Только владелец может редактировать");
        if (partial.getName() != null) existing.setName(partial.getName());
        if (partial.getDescription() != null) existing.setDescription(partial.getDescription());
        if (partial.getAvailable() != null) existing.setAvailable(partial.getAvailable());
        return ItemMapper.toDto(items.update(existing));
    }

    @Override
    public ItemDto get(Long itemId, Long requesterId) {
        Item i = items.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        return ItemMapper.toDto(i);
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        users.findById(ownerId).orElseThrow(() -> new NotFoundException("Владелец не найден"));
        return items.findAllByOwnerId(ownerId).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return items.searchAvailableByText(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }
}
