package ru.practicum.shareit.item;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemWithBookingsDto itemWithBookingsDto;
    private CommentDto commentDto;

    @BeforeEach
    void setup() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Power tool")
                .available(true)
                .requestId(null)
                .build();

        BookingShortDto lastBooking = BookingShortDto.builder()
                .id(100L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .bookerId(2L)
                .build();

        BookingShortDto nextBooking = BookingShortDto.builder()
                .id(101L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .bookerId(3L)
                .build();

        commentDto = CommentDto.builder()
                .id(10L)
                .text("Great!")
                .authorName("User")
                .created(LocalDateTime.now())
                .build();

        itemWithBookingsDto = ItemWithBookingsDto.builder()
                .id(1L)
                .name("Drill")
                .description("Power tool")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(commentDto))
                .build();
    }

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        when(itemService.create(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"));

        verify(itemService).create(eq(1L), any());
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(itemService).update(eq(1L), eq(1L), any());
    }

    @Test
    void getItemById_shouldReturnItemWithBookings() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemWithBookingsDto);

        mockMvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastBooking.id").value(100L))
                .andExpect(jsonPath("$.nextBooking.id").value(101L))
                .andExpect(jsonPath("$.comments[0].id").value(10L));

        verify(itemService).getById(eq(1L), eq(1L));
    }

    @Test
    void getOwnerItems_shouldReturnList() throws Exception {
        when(itemService.getUserItems(anyLong())).thenReturn(List.of(itemWithBookingsDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].lastBooking.id").value(100L));

        verify(itemService).getUserItems(eq(1L));
    }

    @Test
    void searchItems_shouldReturnList() throws Exception {
        when(itemService.search(anyString())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Drill"));

        verify(itemService).search(eq("drill"));
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.text").value("Great!"))
                .andExpect(jsonPath("$.authorName").value("User"));

        verify(itemService).addComment(eq(1L), eq(1L), any());
    }
}

