package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService service;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader(USER_HEADER) Long userId,
                                                 @RequestBody ItemRequestDto dto) {
        ItemRequestDto saved = service.create(userId, dto);
        return ResponseEntity.created(URI.create("/requests/" + saved.getId())).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getOwn(@RequestHeader(USER_HEADER) Long userId) {
        return ResponseEntity.ok(service.getOwn(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAll(@RequestHeader(USER_HEADER) Long userId) {
        return ResponseEntity.ok(service.getAll(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemRequestDto> get(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(userId, id));
    }
}
