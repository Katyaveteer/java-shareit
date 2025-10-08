package ru.practicum.shareit.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerGatewayTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    private ItemRequestDto validRequest;
    private ItemRequestDto invalidRequest;

    @BeforeEach
    void setUp() {
        validRequest = ItemRequestDto.builder()
                .description("Нужна камера")
                .build();

        invalidRequest = ItemRequestDto.builder()
                .description("") // пустое описание
                .build();
    }

    @Test
    void shouldReturn400_whenDescriptionIsBlank() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCallRequestClient_whenDescriptionIsValid() throws Exception {
        when(requestClient.createRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(null);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(requestClient).createRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void shouldCallGetOwnRequests() throws Exception {
        when(requestClient.getOwnRequests(anyLong())).thenReturn(null);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getOwnRequests(anyLong());
    }

    @Test
    void shouldCallGetAllRequests() throws Exception {
        when(requestClient.getAllRequests(anyLong(), any(), any())).thenReturn(null);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(requestClient).getAllRequests(anyLong(), any(), any());
    }

    @Test
    void shouldCallGetRequestById() throws Exception {
        when(requestClient.getRequestById(anyLong(), anyLong())).thenReturn(null);

        mockMvc.perform(get("/requests/5")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getRequestById(anyLong(), anyLong());
    }
}

