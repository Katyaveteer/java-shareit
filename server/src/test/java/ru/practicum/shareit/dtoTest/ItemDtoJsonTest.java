package ru.practicum.shareit.dtoTest;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void shouldSerializeItemDto() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(2L)
                .build();

        var jsonContent = json.write(dto);

        assertThat(jsonContent).hasJsonPath("$.id", 1);
        assertThat(jsonContent).hasJsonPath("$.name", "Drill");
        assertThat(jsonContent).hasJsonPath("$.description", "Power drill");
        assertThat(jsonContent).hasJsonPath("$.available", true);
        assertThat(jsonContent).hasJsonPath("$.requestId", 2);
    }

    @Test
    void shouldDeserializeItemDto() throws Exception {
        String jsonString = """
                {
                  "id": 1,
                  "name": "Drill",
                  "description": "Power drill",
                  "available": true,
                  "requestId": 2
                }
                """;

        var dto = json.parse(jsonString).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Power drill");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(2L);
    }
}