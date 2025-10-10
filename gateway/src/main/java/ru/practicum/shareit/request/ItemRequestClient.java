package ru.practicum.shareit.request;



import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;


@Component
public class ItemRequestClient extends BaseClient {

    private static final String REQUESTS_PATH = "/requests";

    public ItemRequestClient(@Qualifier("gatewayRestTemplate") RestTemplate restTemplate,
                             @Value("${shareit.server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    // Создание нового запроса
    public ResponseEntity<Object> create(Long userId, ItemRequestCreateDto requestDto) {
        return post(REQUESTS_PATH, userId, null, requestDto);
    }

    // Получение всех своих запросов
    public ResponseEntity<Object> getOwnRequests(Long userId) {
        return get(REQUESTS_PATH, userId, null);
    }

    // Получение всех запросов (в том числе чужих)
    public ResponseEntity<Object> getAllRequests(Long userId) {
        return get(REQUESTS_PATH + "/all", userId, null);
    }

    // Получение запроса по ID
    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get(REQUESTS_PATH + "/" + requestId, userId, null);
    }
}
