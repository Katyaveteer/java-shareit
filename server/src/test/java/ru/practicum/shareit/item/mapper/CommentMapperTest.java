package ru.practicum.shareit.item.mapper;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toDto_shouldMapCommentToCommentDto() {
        User author = new User();
        author.setId(1L);
        author.setName("John");
        Item item = new Item();
        item.setId(2L);
        Comment comment = Comment.builder()
                .id(3L)
                .text("Great!")
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        CommentDto dto = CommentMapper.toDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getAuthor().getName(), dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }
}

