package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.model.compilation.dto.NewCompilationDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventDtoResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompilationMapper {

    EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Named("eventToResponse")
    default EventDtoResponse eventToResponse(Event event) {
        return eventMapper.eventToEventDtoResponse(event);
    }

    @Mapping(target = "events", ignore = true)
    CompilationResponseDto newToComplitaionResponse(NewCompilationDto newCompilationDto);

    @Mapping(target = "events", source = "compilation.events", qualifiedByName = "eventToResponse")
    CompilationResponseDto compilationToCompilationResponse(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    Compilation convertToCompilation(NewCompilationDto compilationDto);
}
