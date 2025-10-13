package ru.practicum.shareit.integration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void shouldCreateItemRequest() {
        UserDto user = userService.create(new UserDto(null, "Alice", "alice@example.com"));
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a drill");

        ItemRequestDto saved = requestService.create(user.getId(), createDto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Need a drill");
        assertThat(saved.getCreated()).isNotNull();
        assertThat(saved.getItems()).isEmpty();
    }

    @Test
    void shouldGetOwnRequests() {
        UserDto user = userService.create(new UserDto(null, "Alice", "alice@example.com"));
        requestService.create(user.getId(), new ItemRequestCreateDto("Need drill"));
        requestService.create(user.getId(), new ItemRequestCreateDto("Need saw"));

        List<ItemRequestDto> requests = requestService.getOwnRequests(user.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need saw"); // новее выше
        assertThat(requests.get(1).getDescription()).isEqualTo("Need drill");
    }

    @Test
    void shouldGetAllRequestsFromOtherUsers() {
        UserDto user1 = userService.create(new UserDto(null, "Alice", "alice@example.com"));
        UserDto user2 = userService.create(new UserDto(null, "Bob", "bob@example.com"));

        requestService.create(user1.getId(), new ItemRequestCreateDto("Need drill")); // от Alice
        requestService.create(user2.getId(), new ItemRequestCreateDto("Need saw"));   // от Bob

        List<ItemRequestDto> requests = requestService.getAllRequests(user1.getId()); // Alice видит чужие

        assertThat(requests).hasSize(1);
        assertThat(requests.getFirst().getDescription()).isEqualTo("Need saw");
    }

    @Test
    void shouldGetRequestByIdWithItems() {
        UserDto owner = userService.create(new UserDto(null, "Owner", "owner@example.com"));
        UserDto requester = userService.create(new UserDto(null, "Requester", "requester@example.com"));

        ItemRequestDto request = requestService.create(requester.getId(), new ItemRequestCreateDto("Need drill"));

        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(request.getId())
                .build();
        itemService.create(owner.getId(), itemDto);

        ItemRequestDto retrieved = requestService.getRequestById(requester.getId(), request.getId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getItems()).hasSize(1);
        assertThat(retrieved.getItems().getFirst().getName()).isEqualTo("Drill");
    }

    @Test
    void shouldThrowNotFoundWhenUserDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> requestService.create(999L, new ItemRequestCreateDto("Need drill")));
    }

    @Test
    void shouldThrowNotFoundWhenRequestNotFound() {
        UserDto user = userService.create(new UserDto(null, "Alice", "alice@example.com"));
        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(user.getId(), 999L));
    }
}
