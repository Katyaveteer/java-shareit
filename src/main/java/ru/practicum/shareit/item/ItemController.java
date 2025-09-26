package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
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
    public ResponseEntity<ItemDto> create(@RequestHeader(USER_HEADER) Long ownerId,
                                          @Valid @RequestBody ItemDto dto) {
        ItemDto saved = service.create(ownerId, dto);
        return ResponseEntity.created(URI.create("/items/" + saved.getId())).body(saved);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(USER_HEADER) Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemDto dto) {
        return ResponseEntity.ok(service.update(userId, itemId, dto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithBookingsDto> get(@RequestHeader(USER_HEADER) Long userId,
                                                   @PathVariable Long itemId) {
        return ResponseEntity.ok(service.getById(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemWithBookingsDto>> ownerItems(@RequestHeader(USER_HEADER) Long userId) {
        return ResponseEntity.ok(service.getUserItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        return ResponseEntity.ok(service.search(text));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }
}
