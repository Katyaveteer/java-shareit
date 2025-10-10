package ru.practicum.shareit.dtoTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void shouldSerializeBookingDto() throws Exception {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 6, 1, 10, 0))
                .end(LocalDateTime.of(2024, 6, 2, 10, 0))
                .status(BookingStatus.APPROVED)
                .build();

        var jsonContent = json.write(dto);

        assertThat(jsonContent).hasJsonPath("$.id", 1);
        assertThat(jsonContent).hasJsonPath("$.start", "2024-06-01T10:00:00");
        assertThat(jsonContent).hasJsonPath("$.end", "2024-06-02T10:00:00");
        assertThat(jsonContent).hasJsonPath("$.status", "APPROVED");
    }

    @Test
    void shouldDeserializeBookingDto() throws Exception {
        String jsonString = "{"
                + "\"id\": 1,"
                + "\"start\": \"2024-06-01T10:00:00\","
                + "\"end\": \"2024-06-02T10:00:00\","
                + "\"status\": \"APPROVED\""
                + "}";
        var dto = json.parse(jsonString).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 0));
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

}
