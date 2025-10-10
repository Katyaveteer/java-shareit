package ru.practicum.shareit.integration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldCreateUser() {
        UserDto dto = new UserDto(null, "Alice", "alice@example.com");
        UserDto saved = userService.create(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Alice");
        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void shouldNotCreateUserWithDuplicateEmail() {
        userService.create(new UserDto(null, "Alice", "alice@example.com"));
        UserDto duplicate = new UserDto(null, "Bob", "alice@example.com");

        assertThrows(ru.practicum.shareit.exception.ConflictException.class,
                () -> userService.create(duplicate));
    }

    @Test
    void shouldUpdateUser() {
        UserDto created = userService.create(new UserDto(null, "Alice", "alice@example.com"));
        UserDto update = new UserDto(null, "Alice Updated", "alice2@example.com");

        UserDto updated = userService.update(created.getId(), update);

        assertThat(updated.getName()).isEqualTo("Alice Updated");
        assertThat(updated.getEmail()).isEqualTo("alice2@example.com");
    }

    @Test
    void shouldGetUserById() {
        UserDto created = userService.create(new UserDto(null, "Alice", "alice@example.com"));
        UserDto found = userService.get(created.getId());

        assertThat(found.getName()).isEqualTo("Alice");
    }

    @Test
    void shouldDeleteUser() {
        UserDto created = userService.create(new UserDto(null, "Alice", "alice@example.com"));
        userService.delete(created.getId());

        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> userService.get(created.getId()));
    }
}
