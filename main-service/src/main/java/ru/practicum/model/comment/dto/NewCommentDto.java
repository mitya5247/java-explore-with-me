package ru.practicum.model.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {

    @NotBlank
    @Size(max = 500)
    String text;
    @NotBlank
    Integer author;
    @NotBlank
    Integer event;
    String timestamp;
}
