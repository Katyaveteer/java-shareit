package ru.practicum.shareit.item.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    ItemRequest request;
}
