package ru.practicum.shareit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient client;

    @Test
    void shouldCreateItem() throws Exception {
        ItemDto dto = ItemDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .build();

        when(client.create(anyLong(), any(ItemDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok(dto));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSearchItems() throws Exception {
        when(client.search(anyString()))
                .thenReturn(org.springframework.http.ResponseEntity.ok(java.util.List.of()));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(client.get(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "name", "Drill")));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON)) // ← добавлено
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetOwnerItems() throws Exception {
        when(client.ownerItems(anyLong()))
                .thenReturn(org.springframework.http.ResponseEntity.ok(java.util.List.of()));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateItem() throws Exception {
        ItemDto dto = ItemDto.builder().name("Updated").build();

        when(client.update(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok(dto));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentDto commentDto =
                CommentDto.builder()
                        .text("Great item!")
                        .build();

        when(client.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok(commentDto));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }
}
