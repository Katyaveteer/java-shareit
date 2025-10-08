package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(UserServiceImpl.class)
public class UserServiceImplTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    private UserDto userDto;

    @BeforeEach
    void setup() {
        userDto = UserDto.builder()
                .name("Oleg")
                .email("oleg@mail.ru")
                .build();
    }

    @Test
    void createUser_shouldSaveAndReturnUser() {
        UserDto saved = userService.create(userDto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Oleg");
        assertThat(saved.getEmail()).isEqualTo("oleg@mail.ru");

        assertThat(userRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void createUser_shouldThrowConflictException_whenEmailAlreadyExists() {
        userService.create(userDto);
        UserDto sameEmail = UserDto.builder()
                .name("Ivan")
                .email("oleg@mail.ru")
                .build();

        assertThrows(ConflictException.class, () -> userService.create(sameEmail));
    }

    @Test
    void updateUser_shouldUpdateEmailAndName() {
        User saved = userRepository.save(new User(null, "Oleg", "oleg@mail.ru"));
        UserDto update = UserDto.builder()
                .name("Alex")
                .email("alex@mail.ru")
                .build();

        UserDto updated = userService.update(saved.getId(), update);

        assertThat(updated.getName()).isEqualTo("Alex");
        assertThat(updated.getEmail()).isEqualTo("alex@mail.ru");
    }

    @Test
    void updateUser_shouldThrowNotFound_whenNoUser() {
        UserDto update = UserDto.builder().name("Ghost").build();

        assertThrows(NotFoundException.class, () -> userService.update(999L, update));
    }

    @Test
    void getAll_shouldReturnListOfUsers() {
        userRepository.saveAll(List.of(
                new User(null, "Oleg", "oleg@mail.ru"),
                new User(null, "Pavel", "pavel@mail.ru")
        ));

        List<UserDto> users = userService.getAll();

        assertThat(users).hasSize(2);
        assertThat(users.getFirst().getName()).isEqualTo("Oleg");
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        User saved = userRepository.save(new User(null, "Oleg", "oleg@mail.ru"));
        userService.delete(saved.getId());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void deleteUser_shouldThrowNotFound_whenUserDoesNotExist() {
        assertThrows(NotFoundException.class, () -> userService.delete(777L));
    }
}

