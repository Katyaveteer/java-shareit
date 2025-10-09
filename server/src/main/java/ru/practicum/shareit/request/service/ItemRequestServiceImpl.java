package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepository item;
    private final RequestRepository repo;
    private final UserRepository users;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        User user = users.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = ItemRequest.builder()
                .description(dto.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        repo.save(request);
        return ItemRequestMapper.toItemRequestDto(request, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getOwn(Long userId) {
        users.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> requests = repo.findAllByRequesterIdOrderByCreatedDesc(userId);
        return toDtoList(requests);
    }


    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getById(Long userId, Long requestId) {
        users.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = repo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        List<Item> items = item.findAllByRequestId(requestId);
        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAll(Long userId) {
        users.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> allRequests = repo.findAllByRequesterIdNotOrderByCreatedDesc(userId);

        return toDtoList(allRequests);
    }

    private List<ItemRequestDto> toDtoList(List<ItemRequest> requests) {
        Map<Long, List<Item>> itemsByRequest = item.findAllByRequestIdIn(
                        requests.stream().map(ItemRequest::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request,
                        itemsByRequest.getOrDefault(request.getId(), List.of())))
                .collect(Collectors.toList());
    }
}
