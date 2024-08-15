package ru.practicum.model.event.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.category.Category;
import ru.practicum.model.user.User;
import ru.practicum.model.location.dto.LocationDto;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDtoResponse {

    String annotation;
    Category category;
    Integer confirmedRequests;
    String createdOn;
    String description;
    String eventDate;
    Integer id;
    User initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    String publishedOn;
    Boolean requestModeration;
    String state;
    String title;
    Integer views;
}
