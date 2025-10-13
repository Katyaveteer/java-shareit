package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient client;

    private ItemRequestDto createTestItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();
    }

    @Test
    void shouldCreateRequest() throws Exception {
        ItemRequestCreateDto createDto = ItemRequestCreateDto.builder()
                .description("Need a drill")
                .build();

        ItemRequestDto responseDto = createTestItemRequestDto();

        when(client.create(anyLong(), any(ItemRequestCreateDto.class)))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void shouldGetOwnRequests() throws Exception {
        ItemRequestDto requestDto = createTestItemRequestDto();
        List<ItemRequestDto> requests = List.of(requestDto);

        when(client.getOwnRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(requests));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldGetAllRequests() throws Exception {
        ItemRequestDto requestDto = createTestItemRequestDto();
        List<ItemRequestDto> requests = List.of(requestDto);

        when(client.getAllRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(requests));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldGetRequestById() throws Exception {
        ItemRequestDto requestDto = createTestItemRequestDto();

        when(client.getById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(requestDto));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateWithEmptyDescription() throws Exception {
        ItemRequestCreateDto invalidDto = ItemRequestCreateDto.builder()
                .description("") // пустое описание
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCreateWithNullDescription() throws Exception {
        String invalidJson = "{}"; // отсутствует description

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
