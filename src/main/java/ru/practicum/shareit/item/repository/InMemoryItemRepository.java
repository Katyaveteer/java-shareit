package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public Item save(Item item) {
        long id = seq.incrementAndGet();
        item.setId(id);
        storage.put(id, item);
        return item;
    }

    @Override
    public Item update(Item item) {
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        return storage.values().stream()
                .filter(i -> i.getOwner() != null && Objects.equals(i.getOwner().getId(), ownerId))
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchAvailableByText(String text) {
        if (text == null || text.isBlank()) return List.of();
        String q = text.toLowerCase();
        return storage.values().stream()
                .filter(i -> Boolean.TRUE.equals(i.getAvailable()))
                .filter(i -> (i.getName() != null && i.getName().toLowerCase().contains(q)) ||
                        (i.getDescription() != null && i.getDescription().toLowerCase().contains(q)))
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }
}
