package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

public class ItemClient extends BaseClient {

    private static final String ITEM_PATH = "/items";

    public ItemClient(@Qualifier("gatewayRestTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }


    public ResponseEntity<Object> create(Long ownerId, ItemDto dto) {
        return post(ITEM_PATH, ownerId, dto);
    }


    public ResponseEntity<Object> update(Long userId, Long itemId, ItemDto dto) {
        return patch(ITEM_PATH + "/" + itemId, userId, dto);
    }


    public ResponseEntity<Object> get(Long userId, Long itemId) {
        return get(ITEM_PATH + "/" + itemId, userId);
    }


    public ResponseEntity<Object> ownerItems(Long userId) {
        return get(ITEM_PATH, userId);
    }


    public ResponseEntity<Object> search(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get(ITEM_PATH + "/search", null, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post(ITEM_PATH + "/" + itemId + "/comment", userId, commentDto);
    }


}
