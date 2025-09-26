package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemValidator;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        ItemValidator.validator(dto);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = dto.getRequestId() != null
                ? requestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new NotFoundException("Запрос не найден"))
                : null;

        Item item = ItemMapper.toEntity(dto, owner, request);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto dto) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Не владелец");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemWithBookingsDto getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository
                    .findFirstByItem_IdAndStartBeforeOrderByEndDesc(itemId, LocalDateTime.now())
                    .map(BookingMapper::toShortDto)
                    .orElse(null);

            nextBooking = bookingRepository
                    .findFirstByItem_IdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now())
                    .map(BookingMapper::toShortDto)
                    .orElse(null);
        }

        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .toList();

        return ItemMapper.toDtoWithBookings(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemWithBookingsDto> getUserItems(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);

        return items.stream()
                .map(item -> {
                    BookingShortDto lastBooking = bookingRepository
                            .findFirstByItem_IdAndStartBeforeOrderByEndDesc(item.getId(), LocalDateTime.now())
                            .map(BookingMapper::toShortDto)
                            .orElse(null);

                    BookingShortDto nextBooking = bookingRepository
                            .findFirstByItem_IdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now())
                            .map(BookingMapper::toShortDto)
                            .orElse(null);

                    List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                            .stream()
                            .map(CommentMapper::toDto)
                            .toList();

                    return ItemMapper.toDtoWithBookings(item, lastBooking, nextBooking, comments);
                })
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) return List.of();
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        // проверяем, было ли у пользователя подтверждённое бронирование, завершившееся в прошлом
        boolean hasBooking = bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()
        );

        if (!hasBooking) {
            throw new BadRequestException("Комментарий можно оставить только после завершённого бронирования");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        return CommentMapper.toDto(commentRepository.save(comment));
    }

}
