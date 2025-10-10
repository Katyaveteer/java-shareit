package ru.practicum.shareit.dtoTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void shouldSerializeItemRequestDto() throws Exception {
        UserDto user = new UserDto(5L, "Alice", "alice@example.com");
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(10L)
                .description("Need a drill")
                .requestor(user)
                .build();

        var jsonContent = json.write(dto);

        assertThat(jsonContent).hasJsonPathValue("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(5);
    }

    @Test
    void shouldDeserializeItemRequestDto() throws Exception {
        String jsonString = """
        {
          "id": 10,
          "description": "Need a drill",
          "requestor": {
            "id": 5,
            "name": "Alice",
            "email": "alice@example.com"
          }
        }
        """;

        var dto = json.parse(jsonString).getObject();

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getRequestor().getId()).isEqualTo(5L);
    }

}

