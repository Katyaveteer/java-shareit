package ru.practicum.shareit.dtoTest;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void shouldSerializeUserDto() throws Exception {
        UserDto dto = UserDto.builder()
                .id(100L)
                .name("Alice")
                .email("alice@example.com")
                .build();

        var jsonContent = json.write(dto);

        assertThat(jsonContent).hasJsonPath("$.id", 100);
        assertThat(jsonContent).hasJsonPath("$.name", "Alice");
        assertThat(jsonContent).hasJsonPath("$.email", "alice@example.com");
    }

    @Test
    void shouldDeserializeUserDto() throws Exception {
        String jsonString = """
        {
          "id": 100,
          "name": "Alice",
          "email": "alice@example.com"
        }
        """;

        var dto = json.parse(jsonString).getObject();

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getName()).isEqualTo("Alice");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
    }

}
