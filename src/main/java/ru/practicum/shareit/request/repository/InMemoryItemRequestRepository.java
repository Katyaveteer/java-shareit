package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRequestRepository {
    private final Map<Long, ItemRequest> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public ItemRequest save(ItemRequest r) {
        long id = seq.incrementAndGet();
        r.setId(id);
        storage.put(id, r);
        return r;
    }

    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<ItemRequest> findAllByRequestorId(Long requestorId) {
        return storage.values().stream()
                .filter(r -> r.getRequestor() != null && Objects.equals(r.getRequestor().getId(), requestorId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }

    public List<ItemRequest> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }
}
