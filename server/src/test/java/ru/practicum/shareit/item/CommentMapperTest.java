package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommentMapperTest {
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    public void beforeEach() {
        comment = new Comment(1L, "Text", new Item(), new User(), null);

        commentDto = CommentDto
                .builder()
                .id(1L)
                .text("It's awesome!")
                .build();
    }

    @Test
    public void toDtoTest() {
        CommentDto dto = CommentMapper.toDto(comment);

        assertEquals(dto.getId(), comment.getId());
        assertEquals(dto.getText(), comment.getText());
    }

    @Test
    public void toEntityTest() {
        Comment newComment = commentMapper.toEntity(commentDto);

        assertEquals(newComment.getId(), commentDto.getId());
        assertEquals(newComment.getText(), commentDto.getText());
    }

    @Test
    public void nullToEntityTest() {
        Comment newComment = commentMapper.toEntity(null);

        assertNull(newComment);
    }
}
