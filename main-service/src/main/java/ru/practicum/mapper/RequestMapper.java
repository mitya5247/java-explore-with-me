package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.model.event.Event;
import ru.practicum.model.request.ParticipationRequest;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestMapper {

    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String time) {
        return LocalDateTime.parse(time, df);
    }

    @Named("eventToId")
    default Integer eventToId(Event event) {
        return event.getId();
    }

    @Named("requesterToId")
    default Integer requesterToId(User user) {
        return user.getId();
    }

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime time) {
        return time.format(df);
    }

    @Mapping(target = "created", source = "requestDto.created", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "requester", ignore = true)
    ParticipationRequest requestDtoToRequest(ParticipationRequestDto requestDto);

    @Mapping(target = "created", source = "request.created", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "event", source = "request.event", qualifiedByName = "eventToId")
    @Mapping(target = "requester", source = "request.requester", qualifiedByName = "requesterToId")
    ParticipationRequestDto requestToDto(ParticipationRequest request);
}
