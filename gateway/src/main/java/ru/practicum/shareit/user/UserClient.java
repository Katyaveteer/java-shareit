package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserClient extends BaseClient {

    private static final String USER_PATH = "/users";

    public UserClient(@Qualifier("gatewayRestTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }


    public ResponseEntity<Object> create(UserDto dto) {
        return post(USER_PATH, dto);
    }


    public ResponseEntity<Object> update(Long id, UserDto dto) {
        return patch(USER_PATH + "/" + id, dto);
    }


    public ResponseEntity<Object> getUser(Long id) {
        return get(USER_PATH + "/" + id);
    }


    public ResponseEntity<Object> all() {
        return get(USER_PATH);
    }


    public ResponseEntity<Object> delete(Long id) {
        return delete(USER_PATH + "/" + id);
    }


}
