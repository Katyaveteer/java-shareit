package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeUserDto() throws Exception {
        // Given
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john@email.com")
                .build();

        // When
        String json = objectMapper.writeValueAsString(userDto);

        // Then
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"John\"");
        assertThat(json).contains("\"email\":\"john@email.com\"");
    }

    @Test
    void shouldDeserializeUserDto() throws Exception {
        // Given
        String json = "{\"id\":1,\"name\":\"John\",\"email\":\"john@email.com\"}";

        // When
        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        // Then
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("John");
        assertThat(userDto.getEmail()).isEqualTo("john@email.com");
    }
}