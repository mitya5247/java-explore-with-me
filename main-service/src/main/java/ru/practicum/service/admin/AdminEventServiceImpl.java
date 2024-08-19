package ru.practicum.service.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventAlreadyPublishedException;
import ru.practicum.exceptions.EventPatchException;
import ru.practicum.exceptions.EventPublicationException;
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
import ru.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminEventServiceImpl implements AdminEventService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    EventMapper eventMapper;
    @Autowired
    LocationMapper locationMapper;
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    StatsClient client;
    String baseUri;

    public AdminEventServiceImpl(EventRepository eventRepository, CategoryRepository categoryRepository,
                                 EventMapper eventMapper, LocationMapper locationMapper, RequestRepository requestRepository,
                                 StatsClient client, @Value("${stats-uri}") String baseUri) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
        this.locationMapper = locationMapper;
        this.requestRepository = requestRepository;
        this.client = client;
        this.baseUri = baseUri;
    }

    @Override
    public List<EventDtoResponse> get(List<Integer> usersId, List<String> states, List<Integer> categoriesId, String start,
                                      String end, Integer from, Integer size) {

        List<Event> events = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Pageable pageable = PageRequest.of(from/size, size);
        List<State> stateList = new ArrayList<>();
        if (usersId == null && states == null && categoriesId == null && start == null && end == null) {
            events = eventRepository.findByEmptyParametres(pageable);
            return events.stream()
                    .map(this::mapToResponse)
                    .map(this::countRequests)
                    .map(this::getStat)
                    .collect(Collectors.toList());
        }
        if (categoriesId == null) {
            categoriesId = new ArrayList<>();
        }
        if (usersId == null) {
            usersId = new ArrayList<>();
        }
        if (states != null) {
            for (String state : states) {
                stateList.add(State.valueOf(state));
            }
        } else {
            stateList = new ArrayList<>();
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
                .map(this::countRequests)
                .map(this::getStat)
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoResponse patch(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest) throws EntityNotFoundException,
            EventPatchException, EventAlreadyPublishedException, EventPublicationException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime moment = LocalDateTime.parse(updateEventAdminRequest.getEventDate(), df);
            if (!moment.isAfter(LocalDateTime.now().plusHours(1))) {
                throw new EventPatchException("Event with id " + eventId + "couldn't be less than 1 hours. Date: " + moment);
            }
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new EventAlreadyPublishedException("Event with id " + eventId + " already published " + event.getState());
        }
        if (event.getState().equals(State.CANCELED)) {
            throw new EventPublicationException("Event with id eventId already published event.getState())");
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

    private EventDtoResponse countRequests(EventDtoResponse eventDtoResponse) {
        eventDtoResponse.setConfirmedRequests(requestRepository.countConfirmedRequests(eventDtoResponse.getId()));
        return eventDtoResponse;
    }

    private EventDtoResponse getStat(EventDtoResponse eventDtoResponse) {
        String uri = baseUri + "/stats?end=2041-01-01 00:00:00&unique=true&&uris=/events/" + eventDtoResponse.getId();
        List<ViewStats> list = this.getStat(uri);
        this.parseViewsForEvent(list, eventDtoResponse);
        if (eventDtoResponse.getViews() == null) {
            eventDtoResponse.setViews(0L);
        }
        return eventDtoResponse;
    }

    @SneakyThrows
    private List<ViewStats> getStat(String uri) {
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<Object> response = client.getRequest(uri);
        Object body = response.getBody();
        String json = mapper.writeValueAsString(body);

        List<ViewStats> responseList = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, ViewStats.class));

        return responseList;
    }

    private void parseViewsForEvent(List<ViewStats> viewStatsList, EventDtoResponse eventDtoResponse) {
        for (ViewStats stats : viewStatsList) {
            eventDtoResponse.setViews(stats.getHits());
        }
    }
}
