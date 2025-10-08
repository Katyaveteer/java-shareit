package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerializeAndDeserialize() throws Exception {
        LocalDateTime now = LocalDateTime.of(2024, 10, 1, 12, 0);
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Good item")
                .authorName("Alex")
                .created(now)
                .build();

        var jsonContent = json.write(dto);

        assertThat(jsonContent).hasJsonPathNumberValue("$.id");
        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo("Good item");
        assertThat(jsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo("Alex");


        CommentDto parsed = json.parseObject(jsonContent.getJson());
        assertThat(parsed.getText()).isEqualTo("Good item");
    }
}
