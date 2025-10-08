package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

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

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder().id(1L).name("Test User").email("test@mail.ru").build();
        request = ItemRequest.builder().id(2L).description("Need a drill").build();
        item = Item.builder()
                .id(10L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(user)
                .request(request)
                .build();

        itemDto = ItemDto.builder()
                .id(10L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .requestId(2L)
                .build();
    }

    @Test
    void create_shouldCreateItemWithRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(2L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto saved = itemService.create(1L, itemDto);

        assertThat(saved.getName()).isEqualTo("Drill");
        assertThat(saved.getRequestId()).isEqualTo(2L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void create_shouldThrowIfUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.create(1L, itemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldUpdateOnlyProvidedFields() {
        ItemDto updateDto = ItemDto.builder().name("NewName").available(false).build();

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto updated = itemService.update(1L, 10L, updateDto);

        assertThat(updated.getName()).isEqualTo("NewName");
        verify(itemRepository).save(any());
    }

    @Test
    void update_shouldThrowIfNotOwner() {
        item.setOwner(User.builder().id(2L).build());
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(1L, 10L, itemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Не владелец");
    }

    @Test
    void getById_shouldReturnItemWithBookingsAndComments() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(10L)).thenReturn(List.of(
                Comment.builder().id(1L).text("ok").author(user).item(item).created(LocalDateTime.now()).build()
        ));

        ItemWithBookingsDto dto = itemService.getById(1L, 10L);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getComments()).hasSize(1);
    }

    @Test
    void search_shouldReturnList_whenTextNotBlank() {
        when(itemRepository.search("drill")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("drill");

        assertThat(result).hasSize(1);
        verify(itemRepository).search("drill");
    }

    @Test
    void search_shouldReturnEmptyList_whenTextBlank() {
        List<ItemDto> result = itemService.search("");
        assertThat(result).isEmpty();
        verifyNoInteractions(itemRepository);
    }

    @Test
    void addComment_shouldAddComment_whenUserHadBooking() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                eq(1L), eq(10L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)
        )).thenReturn(true);
        when(commentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CommentDto dto = itemService.addComment(1L, 10L, new CommentDto(null, "good", null, null));

        assertThat(dto.getText()).isEqualTo("good");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrow_whenNoApprovedBooking() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(false);

        assertThatThrownBy(() -> itemService.addComment(1L, 10L, new CommentDto()))
                .isInstanceOf(BadRequestException.class);
    }
}


