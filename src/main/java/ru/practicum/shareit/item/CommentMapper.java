package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
@Service
public interface CommentMapper {
    @Named("commentToCommentDto")
    public static CommentDto toDto(Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    };

    Comment toEntity(CommentDto commentDto);
}