package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.State;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.EventDtoResponse;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.EventShortDtoDb;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {

    LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String time) {
        return LocalDateTime.parse(time, df);
    }

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime time) {
        return time.format(df);
    }

    @Named("enumToString")
    default String enumToString(Enum<State> value) {
        return value.toString();
    }

    @Named("stringToEnum")
    default Enum<State> stringToEnum(String value) {
        return State.valueOf(value);
    }

    @Mapping(target = "eventDate", source = "eventDto.eventDate", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "category", ignore = true)
    Event eventDtoToEvent(EventDto eventDto);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", source = "event.eventDate", qualifiedByName = "localDateTimeToString")
    EventDto eventToEventDto(Event event);

    @Mapping(target = "eventDate", source = "event.eventDate", qualifiedByName = "localDateTimeToString")
    EventShortDto eventToShortDto(Event event);


    @Mapping(target = "eventDate", source = "event.eventDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "state", source = "event.state", qualifiedByName = "enumToString")
    EventDtoResponse eventToEventDtoResponse(Event event);

    @Mapping(target = "eventDate", source = "eventShortDtoDb.eventDate", qualifiedByName = "localDateTimeToString")
    EventShortDto eventShortDbToShort(EventShortDtoDb eventShortDtoDb);


}
