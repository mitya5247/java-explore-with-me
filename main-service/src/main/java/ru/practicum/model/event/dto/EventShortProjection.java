package ru.practicum.model.event.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public interface EventShortProjection {

    String getAnnotation();

    Integer getConfirmedRequests();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getEventDate();

    Integer getId();

    UserDto getInitiator();

    interface UserDto {
        Integer getId();
        String getName();
    }

    Boolean getPaid();

    String getTitle();

    Integer getViews();

}
