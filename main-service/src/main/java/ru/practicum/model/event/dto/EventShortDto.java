package ru.practicum.model.event.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.category.Category;
import ru.practicum.model.user.dto.UserDto;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    String annotation;
    Category category;
    Integer confirmedRequests;
    String eventDate;
    Integer id;
    UserDto initiator;
    Boolean paid;
    String title;
    Long views;
}
