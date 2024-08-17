package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.model.compilation.dto.NewCompilationDto;
import ru.practicum.model.event.Event;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompilationMapper {

    @Named("eventToId")
    default Integer eventToId(Event event) {
        return event.getId();
    }

    CompilationResponseDto newToComplitaionResponse(NewCompilationDto newCompilationDto);

    @Mapping(target = "events", source = "compilation.events", qualifiedByName = "eventToId")
    CompilationResponseDto compilationToCompilationResponse(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    Compilation convertToCompilation(NewCompilationDto compilationDto);
}
