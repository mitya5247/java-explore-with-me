package ru.practicum.model.request.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ParticipationRequestDto {
    String created;
    Integer event;
    Integer id;
    Integer requester;
    String status;
}
