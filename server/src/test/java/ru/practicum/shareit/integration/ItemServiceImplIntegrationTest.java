package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void shouldGetUserItemsWithBookingsAndComments() {

        UserDto ownerDto = new UserDto(null, "Owner", "owner@example.com");
        UserDto owner = userService.create(ownerDto);

        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .build();
        itemService.create(owner.getId(), itemDto);


        List<ItemWithBookingsDto> items = itemService.getUserItems(owner.getId());

        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getName()).isEqualTo("Drill");
        assertThat(items.getFirst().getDescription()).isEqualTo("Power drill");
        assertThat(items.getFirst().getLastBooking()).isNull();
        assertThat(items.getFirst().getNextBooking()).isNull();
        assertThat(items.getFirst().getComments()).isEmpty();
    }
}
