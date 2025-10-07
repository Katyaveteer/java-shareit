package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void create_shouldSaveUser() {
        // Given
        UserDto userDto = UserDto.builder()
                .name("John")
                .email("john@email.com")
                .build();

        // When
        UserDto result = userService.create(userDto);

        // Then
        assertNotNull(result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@email.com", result.getEmail());
    }

    @Test
    void update_shouldUpdateUser() {
        // Given
        User user = userRepository.save(User.builder()
                .name("John")
                .email("john@email.com")
                .build());

        UserDto updateDto = UserDto.builder()
                .name("John Updated")
                .email("john.updated@email.com")
                .build();

        // When
        UserDto result = userService.update(user.getId(), updateDto);

        // Then
        assertEquals("John Updated", result.getName());
        assertEquals("john.updated@email.com", result.getEmail());
    }
}
