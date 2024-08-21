package ru.practicum.service.publ;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventDtoResponse;
import ru.practicum.repository.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {

    @Autowired
    CompilationRepository repository;
    @Autowired
    EventMapper eventMapper;
    @Autowired
    CompilationMapper compilationMapper;

    @Override
    public List<CompilationResponseDto> get(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<CompilationResponseDto> compilations = repository.findByPinned(pinned, pageable)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return compilations;
    }

    @Override
    public CompilationResponseDto getCompilation(Integer compId) throws EntityNotFoundException {
        Compilation compilation = repository.findById(compId).orElseThrow(() -> new EntityNotFoundException("Compilation with id " + compId +
                " was not found"));
        CompilationResponseDto responseDto = compilationMapper.compilationToCompilationResponse(compilation);
        return responseDto;
    }

    private EventDtoResponse mapToResponse(Event event) {
        return eventMapper.eventToEventDtoResponse(event);
    }

    private CompilationResponseDto convertToResponse(Compilation compilation) {
        List<Event> events = compilation.getEvents();
        List<EventDtoResponse> eventDtoResponses = events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return compilationMapper.compilationToCompilationResponse(compilation);
    }
}
