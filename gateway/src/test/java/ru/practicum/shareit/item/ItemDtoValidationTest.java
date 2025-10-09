package ru.practicum.shareit.item;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailValidation_whenItemWithBookingsDtoHasNullOrBlankFields() {
        ItemWithBookingsDto dto = ItemWithBookingsDto.builder()
                .name("")
                .description("   ")
                .available(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        Set<ConstraintViolation<ItemWithBookingsDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(3); // ожидаем 3 ошибки: name, description, available

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name")
                && v.getMessage().contains("Название вещи не может быть пустым"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description")
                && v.getMessage().contains("Описание не может быть пустым"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("available")
                && v.getMessage().contains("Статус доступности обязателен"));
    }

    @Test
    void shouldFailValidation_whenCommentDtoHasBlankText() {
        CommentDto commentDto = CommentDto.builder()
                .text("  ") // пустой текст
                .authorName("John")
                .created(null)
                .build();

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        assertThat(violations).hasSize(1);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("text")
                && v.getMessage().contains("Текст комментария не может быть пустым"));
    }

    @Test
    void shouldPassValidation_whenItemWithBookingsDtoAndCommentDtoAreValid() {
        ItemWithBookingsDto validItem = ItemWithBookingsDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .comments(List.of(new CommentDto(1L, "Комментарий", "Author", null)))
                .build();

        Set<ConstraintViolation<ItemWithBookingsDto>> violationsItem = validator.validate(validItem);
        assertThat(violationsItem).isEmpty();

        CommentDto validComment = new CommentDto(1L, "Хороший текст", "Author", null);
        Set<ConstraintViolation<CommentDto>> violationsComment = validator.validate(validComment);
        assertThat(violationsComment).isEmpty();
    }
}

