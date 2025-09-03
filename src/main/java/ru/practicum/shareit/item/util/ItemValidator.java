package ru.practicum.shareit.item.util;

import jakarta.validation.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemValidator {

    public static void validator(ItemDto itemDto) {

        if (itemDto == null) {
            throw new ValidationException("ItemValidator: item не может быть null");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("ItemValidator: статус доступа не может быть пустым");
        }

        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ValidationException("ItemValidator: название не может быть null");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("ItemValidator: описание не может быть null");
        }


    }
}

