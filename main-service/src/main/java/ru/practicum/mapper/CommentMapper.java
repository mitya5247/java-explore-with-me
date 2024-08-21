package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.dto.CommentDtoResponse;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Named("eventToInt")
    default Integer eventToInt(Event event) {
        return event.getId();
    }

    @Named("userToInt")
    default Integer userToInt(User user) {
        return user.getId();
    }

    @Mapping(target = "event", source = "event", qualifiedByName = "eventToInt")
    @Mapping(target = "author", source = "author", qualifiedByName = "userToInt")
    CommentDtoResponse convertToResponse(Comment comment);
}
