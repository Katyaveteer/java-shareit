package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getUserItems_shouldReturnUserItems() {
        // Given
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@email.com")
                .build());

        ItemDto itemDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        itemService.create(owner.getId(), itemDto);

        // When
        var result = itemService.getUserItems(owner.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Item", result.getFirst().getName());
    }

    @Test
    void search_shouldFindAvailableItems() {
        // Given
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@email.com")
                .build());

        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        itemService.create(owner.getId(), itemDto);

        // When
        var result = itemService.search("drill");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Drill", result.getFirst().getName());
    }
}
