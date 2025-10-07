package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_shouldCreateItemWhenUserExists() {
        // Given
        Long ownerId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        User owner = User.builder().id(ownerId).build();
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });

        // When
        ItemDto result = itemService.create(ownerId, itemDto);

        // Then
        assertNotNull(result);
        assertEquals("Item", result.getName());
        verify(userRepository).findById(ownerId);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void create_shouldThrowExceptionWhenUserNotFound() {
        // Given
        Long ownerId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> itemService.create(ownerId, itemDto));
        verify(userRepository).findById(ownerId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_shouldUpdateItemWhenUserIsOwner() {
        // Given
        Long ownerId = 1L;
        Long itemId = 1L;
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        User owner = User.builder().id(ownerId).build();
        Item existingItem = Item.builder()
                .id(itemId)
                .name("Original Name")
                .description("Original Description")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        // When
        ItemDto result = itemService.update(ownerId, itemId, updateDto);

        // Then
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.getAvailable());
        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(any(Item.class));
    }
}
