package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) Long ownerId,
                                         @Valid @RequestBody ItemDto dto) {
        return client.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_HEADER) Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody ItemDto dto) {
        return client.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getOwner(@RequestHeader(USER_HEADER) Long userId,
                                           @PathVariable Long itemId) {
        return client.get(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> ownerItems(@RequestHeader(USER_HEADER) Long userId) {
        return client.ownerItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return ResponseEntity.ok(client.search(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        ResponseEntity<Object> response = client.addComment(userId, itemId, commentDto);

        if (response.getStatusCode().isError()) {
            return ResponseEntity.status(response.getStatusCode())
                    .body(response.getBody());
        }

        return response;
    }
}