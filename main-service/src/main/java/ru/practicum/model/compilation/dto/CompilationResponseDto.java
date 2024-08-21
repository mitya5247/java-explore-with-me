package ru.practicum.model.compilation.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.event.dto.EventDtoResponse;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationResponseDto {

    List<EventDtoResponse> events;
    Integer id;
    Boolean pinned;
    String title;
}
