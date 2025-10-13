package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(UserDto dto) {
        return post("", dto); // путь пустой, baseUrl уже /users
    }

    public ResponseEntity<Object> update(Long id, UserDto dto) {
        return patch("/" + id, dto);
    }

    public ResponseEntity<Object> getUser(Long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> all() {
        return get("");
    }

    public ResponseEntity<Object> delete(Long id) {
        return delete("/" + id);
    }
}

