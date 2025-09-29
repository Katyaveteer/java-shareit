package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository repo;
    private final UserRepository users;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        users.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest r = ItemRequestMapper.fromDto(dto, userId);
        ItemRequest saved = repo.save(r);
        return ItemRequestMapper.toDto(saved);
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        users.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return repo.findByRequesterId(userId, sort).stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        users.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest r = repo.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
        return ItemRequestMapper.toDto(r);
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        users.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return repo.findAll().stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }
}
