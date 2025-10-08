package ru.practicum.shareit.user.mapper;


import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(User u) {
        return u == null ? null : UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .build();
    }

    public static User fromDto(UserDto d) {
        return d == null ? null : User.builder()
                .id(d.getId())
                .name(d.getName())
                .email(d.getEmail())
                .build();
    }

    public static UserShortDto toShortDto(UserDto dto) {
        return UserShortDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}