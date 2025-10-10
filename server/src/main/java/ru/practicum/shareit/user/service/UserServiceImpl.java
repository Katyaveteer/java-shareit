package ru.practicum.shareit.user.service;

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

        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            UserValidator.validator(dto);

            repo.findByEmail(dto.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new ConflictException("Пользователь с таким email уже существует");
                }
            });

            existing.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        User updated = repo.save(existing); // save работает для обновления
        return UserMapper.toDto(updated);
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
        if (!repo.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        repo.deleteById(id);
    }

}
