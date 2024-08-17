package ru.practicum.service.admin;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.model.compilation.dto.NewCompilationDto;
import ru.practicum.model.compilation.dto.UpdateCompilationDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventDtoResponse;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {

    @Autowired
    CompilationRepository compilationRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    CompilationMapper compilationMapper;
    @Autowired
    EventMapper eventMapper;

    @Override
    public CompilationResponseDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.convertToCompilation(newCompilationDto);
        List<Event> events = this.findEvents(newCompilationDto.getEvents());
        compilation.setEvents(events);
        compilation = compilationRepository.save(compilation);
        return this.convertToResponse(compilation);
    }

    @Override
    public CompilationResponseDto patch(Integer compId, UpdateCompilationDto updateCompilationDto) throws EntityNotFoundException {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new EntityNotFoundException("Compilation with id " + compId +
                " was not found"));
        this.fillFields(compilation, updateCompilationDto);
        if (updateCompilationDto.getEvents() != null) {
            compilation.setEvents(this.findEvents(updateCompilationDto.getEvents()));
        }
        compilation = compilationRepository.save(compilation);
        return this.convertToResponse(compilation);
    }

    @Override
    public void delete(Integer compId) throws EntityNotFoundException {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new EntityNotFoundException("Compilation with id " + compId +
                " was not found"));
        compilationRepository.delete(compilation);
    }

    private List<Event> findEvents(List<Integer> eventsId) {
        return eventRepository.findAllByIdIn(eventsId);
    }

    private Compilation fillFields(Compilation compilation, UpdateCompilationDto compilationDto) {
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        return compilation;
    }

    private EventDtoResponse mapToResponse(Event event) {
        return eventMapper.eventToEventDtoResponse(event);
    }

    private CompilationResponseDto convertToResponse(Compilation compilation) {
        List<Event> events = compilation.getEvents();
        CompilationResponseDto compilationResponseDto = compilationMapper.compilationToCompilationResponse(compilation);
        List<EventDtoResponse> eventDtoResponses = events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        compilationResponseDto.setEvents(eventDtoResponses);
        return compilationResponseDto;
    }
}
