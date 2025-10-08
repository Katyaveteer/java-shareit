package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private User user;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "Alice", "a@mail.com");
        request = new ItemRequest(1L, "Need drill", user, LocalDateTime.now());
    }

    @Test
    void createRequest_shouldSaveAndReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(request);

        ItemRequestDto dto = new ItemRequestDto(null, "Need drill", null, List.of());
        ItemRequestDto result = service.create(1L, dto);

        assertThat(result.getDescription()).isEqualTo("Need drill");
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void createRequest_userNotFound_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(1L, new ItemRequestDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getUserRequests_shouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of());

        List<ItemRequestDto> result = service.getOwn(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDescription()).isEqualTo("Need drill");
    }

    @Test
    void getRequestById_shouldReturnRequestWithItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        Item item = new Item(10L, "Drill", "Good", true, user, request);
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));

        ItemRequestDto result = service.getById(1L, 1L);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().getName()).isEqualTo("Drill");
    }
}

