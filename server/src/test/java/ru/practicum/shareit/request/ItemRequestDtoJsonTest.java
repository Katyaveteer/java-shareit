package ru.practicum.shareit.request;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 10, 1, 12, 0);
        ItemResponseDto item = new ItemResponseDto(1L, "Drill", 2L);
        ItemRequestDto dto = new ItemRequestDto(5L, "Need drill", created, List.of(item));

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need drill");
        assertThat(result).extractingJsonPathStringValue("$.created").contains("2025-10-01T12:00");
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Drill");
    }
}
