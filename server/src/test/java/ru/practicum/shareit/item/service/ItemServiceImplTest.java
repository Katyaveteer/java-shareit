package ru.practicum.shareit.item.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl service;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private User owner;
    private User booker;
    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");

        item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Power tool")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Power tool")
                .available(true)
                .requestId(null)
                .build();

        commentDto = CommentDto.builder()
                .text("Great!")
                .build();
    }

    @Test
    void createItem_shouldReturnItemDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto created = service.create(1L, itemDto);

        assertEquals(itemDto.getName(), created.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_userNotFound_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.create(1L, itemDto));
    }

    @Test
    void updateItem_shouldUpdateFields() {
        Item updatedItem = Item.builder()
                .id(1L)
                .name("Old")
                .description("Old Desc")
                .available(false)
                .owner(owner)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(updatedItem));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto updated = service.update(1L, 1L, itemDto);

        assertEquals("Drill", updated.getName());
        assertEquals("Power tool", updated.getDescription());
        assertTrue(updated.getAvailable());
    }

    @Test
    void updateItem_notOwner_shouldThrow() {
        User other = new User();
        other.setId(99L);
        Item i = Item.builder().id(1L).owner(other).build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(i));

        assertThrows(NotFoundException.class, () -> service.update(1L, 1L, itemDto));
    }

    @Test
    void getById_owner_shouldIncludeBookingsAndComments() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItem_IdAndStartBeforeOrderByEndDesc(anyLong(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItem_IdAndStartAfterOrderByStartAsc(anyLong(), any()))
                .thenReturn(Optional.empty());
        when(commentRepository.findByItemId(1L)).thenReturn(List.of());

        ItemWithBookingsDto dto = service.getById(1L, 1L);
        assertEquals(1L, dto.getId());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertTrue(dto.getComments().isEmpty());
    }

    @Test
    void getById_itemNotFound_shouldThrow() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById(1L, 1L));
    }

    @Test
    void getUserItems_shouldReturnList() {
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findFirstByItem_IdAndStartBeforeOrderByEndDesc(anyLong(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItem_IdAndStartAfterOrderByStartAsc(anyLong(), any()))
                .thenReturn(Optional.empty());
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of());

        List<ItemWithBookingsDto> items = service.getUserItems(1L);
        assertEquals(1, items.size());
    }

    @Test
    void search_blankText_shouldReturnEmpty() {
        List<ItemDto> result = service.search("  ");
        assertTrue(result.isEmpty());
    }

    @Test
    void search_nonBlankText_shouldCallRepository() {
        when(itemRepository.search("drill")).thenReturn(List.of(item));
        List<ItemDto> result = service.search("drill");
        assertEquals(1, result.size());
        verify(itemRepository).search("drill");
    }

    @Test
    void addComment_success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                anyLong(), anyLong(), eq(BookingStatus.APPROVED), any()))
                .thenReturn(true);
        Comment savedComment = Comment.builder()
                .id(10L)
                .text("Great!")
                .author(booker)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = service.addComment(2L, 1L, commentDto);

        assertEquals(10L, result.getId());
        assertEquals("Great!", result.getText());
    }

    @Test
    void addComment_noBooking_shouldThrow() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                anyLong(), anyLong(), eq(BookingStatus.APPROVED), any()))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.addComment(2L, 1L, commentDto));
    }
}

