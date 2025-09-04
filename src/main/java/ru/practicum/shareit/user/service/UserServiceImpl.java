package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.util.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;

    @Override
    public UserDto create(UserDto dto) {
        UserValidator.validator(dto);

        repo.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new ConflictException("Пользователь с таким email уже существует");
        });
        User saved = repo.save(UserMapper.fromDto(dto));
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        User existing = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        repo.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new ConflictException("Пользователь с таким email уже существует");
        });

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            UserValidator.validator(dto);

            repo.findByEmail(dto.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new ValidationException("Адрес электронной почты уже существует");
                }
            });

            existing.setEmail(dto.getEmail());
        }

        return UserMapper.toDto(repo.update(existing));
    }

    @Override
    public UserDto get(Long id) {
        return UserMapper.toDto(repo.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Override
    public List<UserDto> getAll() {
        return repo.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        repo.delete(id);
    }
}
