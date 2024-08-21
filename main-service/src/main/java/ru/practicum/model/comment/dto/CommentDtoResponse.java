package ru.practicum.model.comment.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDtoResponse {

    String text;
    Integer author;
    Integer event;
    String timestamp;
}
