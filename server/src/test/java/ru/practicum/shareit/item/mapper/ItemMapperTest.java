package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toDto_shouldMapItemToItemDto() {
        User owner = new User();
        owner.setId(1L);
        ItemRequest request = new ItemRequest();
        request.setId(2L);
        Item item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Power tool")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        ItemDto dto = ItemMapper.toDto(item);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(item.getRequest(), dto.getRequestId());
    }

    @Test
    void toEntity_shouldMapDtoToItem() {
        User owner = new User();
        owner.setId(1L);
        ItemRequest request = new ItemRequest();
        request.setId(2L);
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Power tool")
                .available(true)
                .requestId(2L)
                .build();

        Item item = ItemMapper.toEntity(dto, owner, request);
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void toDtoWithBookings_shouldMapToItemWithBookingsDto() {
        Item item = Item.builder().id(1L).name("Drill").description("Power tool").available(true).build();
        BookingShortDto lastBooking = BookingShortDto.builder().id(10L).build();
        BookingShortDto nextBooking = BookingShortDto.builder().id(11L).build();
        CommentDto comment = CommentDto.builder().id(5L).text("Nice").build();

        ItemWithBookingsDto dto = ItemMapper.toDtoWithBookings(item, lastBooking, nextBooking, List.of(comment));

        assertEquals(item.getId(), dto.getId());
        assertEquals(lastBooking, dto.getLastBooking());
        assertEquals(nextBooking, dto.getNextBooking());
        assertEquals(1, dto.getComments().size());
    }
}

