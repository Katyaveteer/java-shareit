package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient client;

    private UserDto createTestUser() {
        return UserDto.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserDto dto = UserDto.builder()
                .name("John")
                .email("john@example.com")
                .build();

        UserDto saved = createTestUser();

        when(client.create(any(UserDto.class)))
                .thenReturn(ResponseEntity.status(201)
                        .header("Location", "/users/1")
                        .body(saved));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/1"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserDto dto = UserDto.builder()
                .name("John Updated")
                .email("john2@example.com")
                .build();

        UserDto updatedUser = UserDto.builder()
                .id(1L)
                .name("John Updated")
                .email("john2@example.com")
                .build();

        when(client.update(anyLong(), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(updatedUser));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUserById() throws Exception {
        UserDto user = createTestUser();

        when(client.getUser(anyLong()))
                .thenReturn(ResponseEntity.ok(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        UserDto user1 = createTestUser();
        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("Jane")
                .email("jane@example.com")
                .build();

        List<UserDto> users = List.of(user1, user2);

        when(client.all())
                .thenReturn(ResponseEntity.ok(users));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        when(client.delete(anyLong()))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}