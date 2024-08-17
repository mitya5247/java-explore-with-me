package ru.practicum.model.event.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.location.dto.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDto {

    @Size(min = 20, max = 2000)
    @NotBlank
    String annotation;
    Integer category;
    @Size(min = 20, max = 7000)
    @NotBlank
    String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String eventDate;
    LocationDto location;
    Boolean paid = false;
    @PositiveOrZero
    Integer participantLimit = 0;
    Boolean requestModeration = true;
    @Size(min = 3, max = 120)
    String title;
}
