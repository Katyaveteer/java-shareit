package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService service;

    @Test
    void createUser_shouldReturn201() throws Exception {
        UserDto dto = new UserDto(1L, "Oleg", "oleg@mail.ru");
        when(service.create(any())).thenReturn(dto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/1"))
                .andExpect(jsonPath("$.name").value("Oleg"))
                .andExpect(jsonPath("$.email").value("oleg@mail.ru"));
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {
        UserDto dto = new UserDto(1L, "Oleg", "oleg@mail.ru");
        when(service.update(anyLong(), any())).thenReturn(dto);

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        UserDto dto = new UserDto(1L, "Oleg", "oleg@mail.ru");
        when(service.get(1L)).thenReturn(dto);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("oleg@mail.ru"));
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        List<UserDto> users = List.of(new UserDto(1L, "Oleg", "oleg@mail.ru"));
        when(service.getAll()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Oleg"));
    }

    @Test
    void deleteUser_shouldReturn204() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}

