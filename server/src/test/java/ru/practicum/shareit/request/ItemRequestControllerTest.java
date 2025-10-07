package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void createRequest_shouldReturnCreated() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(1L, "Need hammer", LocalDateTime.now(), List.of());
        when(requestService.create(anyLong(), any())).thenReturn(dto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("Need hammer")));
    }

    @Test
    void getUserRequests_shouldReturnList() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(1L, "Need hammer", LocalDateTime.now(), List.of());
        when(requestService.getOwn(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Need hammer")));
    }

    @Test
    void getAllRequests_shouldReturnList() throws Exception {
        when(requestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(new ItemRequestDto(1L, "Need ladder", LocalDateTime.now(), List.of())));

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Need ladder")));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(1L, "Need hammer", LocalDateTime.now(), List.of());
        when(requestService.getById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Need hammer")));
    }
}
