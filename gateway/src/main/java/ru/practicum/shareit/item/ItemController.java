package ru.practicum.shareit.item;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) Long ownerId,
                                         @Valid @RequestBody ItemDto dto) {

        return itemClient.createItem(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_HEADER) Long userId,
                                         @PathVariable Long itemId,
                                         @Valid @RequestBody ItemDto dto) {

        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_HEADER) Long userId,
                                      @PathVariable Long itemId) {

        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> ownerItems(@RequestHeader(USER_HEADER) Long userId) {

        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {

        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentCreateDto commentDto) {

        return itemClient.addComment(userId, itemId, commentDto);
    }
}
