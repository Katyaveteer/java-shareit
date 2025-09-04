package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader(USER_HEADER) Long userId,
                                          @Valid @RequestBody ItemDto dto) {
        ItemDto saved = service.create(dto, userId);
        return ResponseEntity.created(URI.create("/items/" + saved.getId())).body(saved);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(USER_HEADER) Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemDto partial) {
        return ResponseEntity.ok(service.update(itemId, partial, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> get(@RequestHeader(USER_HEADER) Long userId,
                                       @PathVariable Long itemId) {
        return ResponseEntity.ok(service.get(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> ownerItems(@RequestHeader(USER_HEADER) Long userId) {
        return ResponseEntity.ok(service.getOwnerItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        return ResponseEntity.ok(service.search(text));
    }
}
