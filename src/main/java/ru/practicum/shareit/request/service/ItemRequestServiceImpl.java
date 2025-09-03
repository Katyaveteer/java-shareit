package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.InMemoryItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final InMemoryItemRequestRepository repo;
    private final UserRepository users;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ItemRequest r = ItemRequestMapper.fromDto(dto, userId);
        r.setCreated(LocalDateTime.now());
        ItemRequest saved = repo.save(r);
        return ItemRequestMapper.toDto(saved);
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return repo.findAllByRequestorId(userId).stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ItemRequest r = repo.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        return ItemRequestMapper.toDto(r);
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return repo.findAll().stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }
}
