package ru.practicum.service.admin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventPatchException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.AdminStateAction;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.State;
import ru.practicum.model.event.dto.EventDtoResponse;
import ru.practicum.model.event.dto.UpdateEventAdminRequest;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    EventMapper eventMapper;
    @Autowired
    LocationMapper locationMapper;

    @Override
    public List<EventDtoResponse> get(List<Integer> usersId, List<String> states, List<Integer> categoriesId, String start,
                                      String end, Integer from, Integer size) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Pageable pageable = PageRequest.of(from/size, size);
        List<State> stateList = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        if (usersId == null) {
            usersId = new ArrayList<>();
        }
        if (states == null) {
            states = new ArrayList<>();
        } else {
            stateList = this.convertToState(states);
        }
        if (categoriesId == null) {
            categoriesId = new ArrayList<>();
        }
        if (start == null && end == null) {
            events = eventRepository.findEventByUsersAndStateAndCategory(usersId, stateList, categoriesId, pageable);
        }
        if (start != null && end != null) {
            LocalDateTime startTime = LocalDateTime.parse(start, df);
            LocalDateTime endTime = LocalDateTime.parse(end, df);
            events = eventRepository.findEventByUsersAndStateAndCategoryBetween(usersId, stateList,
                    categoriesId, startTime, endTime, pageable);
        }
        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoResponse patch(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest) throws EntityNotFoundException, EventPatchException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new EventPatchException("Event with id " + eventId + "couldn't be less than 1 hours. Date: " + event.getEventDate());
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new EventPatchException("Event with id " + eventId + "already published " + event.getState());
        }
        this.patchEvent(event, updateEventAdminRequest);
        eventRepository.save(event);
        return eventMapper.eventToEventDtoResponse(event);
    }

    private Event patchEvent(Event event1, UpdateEventAdminRequest eventDto2) throws EntityNotFoundException {
        if (eventDto2.getAnnotation() != null) {
            event1.setAnnotation(eventDto2.getAnnotation());
        }
        if (eventDto2.getEventDate() != null) {
            event1.setEventDate(eventMapper.stringToLocalDateTime(eventDto2.getEventDate()));
        }
        if (eventDto2.getPaid() != null) {
            event1.setPaid(eventDto2.getPaid());
        }
        if (eventDto2.getLocation() != null) {
            event1.setLocation(locationMapper.dtoTolocation(eventDto2.getLocation()));
        }
        if (eventDto2.getDescription() != null) {
            event1.setDescription(eventDto2.getDescription());
        }
        if (eventDto2.getCategory() != null) {
            Category category = categoryRepository.findById(eventDto2.getCategory()).orElseThrow(() ->
                    new EntityNotFoundException("Category with id " + eventDto2.getCategory() +
                            " was not found"));
            event1.setCategory(category);
        }
        if (eventDto2.getTitle() != null) {
            event1.setTitle(eventDto2.getTitle());
        }
        if (eventDto2.getRequestModeration() != null) {
            event1.setRequestModeration(eventDto2.getRequestModeration());
        }
        if (eventDto2.getParticipantLimit() != null) {
            event1.setParticipantLimit(eventDto2.getParticipantLimit());
        }
        if (eventDto2.getStateAction() != null && eventDto2.getStateAction().equals(AdminStateAction.REJECT_EVENT.toString())) {
            event1.setState(State.CANCELED);
        } else  {
            event1.setState(State.PUBLISHED);
            event1.setPublishedOn(LocalDateTime.now());
        }
        return event1;
    }

    private EventDtoResponse mapToResponse(Event event) {
        return eventMapper.eventToEventDtoResponse(event);
    }

    private List<State> convertToState(List<String> stateStringList) {
        List<State> list = new ArrayList<>();
        for (String stateString : stateStringList) {
            list.add(State.valueOf(stateString));
        }
        return list;
    }
}
