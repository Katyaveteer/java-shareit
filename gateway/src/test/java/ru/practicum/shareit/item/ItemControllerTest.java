
package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void create_shouldReturnOkForValidItem() throws Exception {
        // Given
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        when(itemClient.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldReturnBadRequestForInvalidItem() throws Exception {
        // Given
        ItemDto invalidItem = ItemDto.builder()
                .name("")  // Invalid: empty name
                .description("")  // Invalid: empty description
                .available(null)  // Invalid: null available
                .build();

        // When & Then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_shouldReturnOkForValidComment() throws Exception {
        // Given
        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        when(itemClient.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }

    @Test
    void search_shouldReturnOk() throws Exception {
        // Given
        when(itemClient.searchItems("drill"))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk());
    }
}