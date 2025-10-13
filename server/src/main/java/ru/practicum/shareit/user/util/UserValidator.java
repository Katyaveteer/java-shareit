package ru.practicum.shareit.user.util;

import jakarta.validation.ValidationException;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class UserValidator {

    public static void validator(UserDto userDto) {

        if (userDto == null) {
            throw new ValidationException("UserValidator: user не может быть null");
        }

        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            throw new ValidationException("UserValidator: электронная почта не может быть пустой и должна содержать символ @");
        }

    }
}
