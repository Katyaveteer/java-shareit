package ru.practicum.shareit.request;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;


@Component
public class ItemRequestClient extends BaseClient {

    private static final String REQUESTS_PATH = "/requests";

    public ItemRequestClient(@Qualifier("gatewayRestTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, ItemRequestCreateDto requestDto) {
        return post(REQUESTS_PATH, userId, requestDto);
    }

    public ResponseEntity<Object> getOwnRequests(Long userId) {
        return get(REQUESTS_PATH, userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        return get(REQUESTS_PATH + "/all", userId);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get(REQUESTS_PATH + "/" + requestId, userId);
    }
}
