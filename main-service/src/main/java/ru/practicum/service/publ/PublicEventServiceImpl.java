package ru.practicum.service.publ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.Sort;
import ru.practicum.model.event.State;
import ru.practicum.model.event.dto.EventDtoResponse;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PublicEventServiceImpl implements PublicEventService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EventMapper eventMapper;
    @Autowired
    StatsClient client;
    String baseUri;
    HashMap<Integer, Event> eventHashMap = new HashMap<>();

    @Autowired
    RequestRepository requestRepository;

    ObjectMapper mapper = new ObjectMapper();
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public PublicEventServiceImpl(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper,
                                  @Value("${stats-uri}") String baseUri, StatsClient client, RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.client = client;
        this.baseUri = baseUri;
        this.requestRepository = requestRepository;
    }

    @SneakyThrows
    @Override
    public List<EventDtoResponse> get(String textAnnotation, List<Integer> categoriesId, Boolean paid, String rangeStart,
                                      String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                      HttpServletRequest request) throws JsonProcessingException, ValidationException {
        List<Event> events = new ArrayList<>();
        Pageable pageable = PageRequest.of(from, size);

        if (textAnnotation == null) {
            textAnnotation = "";
        }
        if (categoriesId == null) {
            categoriesId = new ArrayList<>();
        }
        if (onlyAvailable == null) {
            onlyAvailable = true;
        }
        if (sort == null) {
            sort = Sort.EVENT_DATE.toString();
        }

        if (rangeStart == null && rangeEnd == null) {
            LocalDateTime moment = LocalDateTime.now();
            events = eventRepository.findEventsByAllCriteriesWithoutTime("%" + textAnnotation.toLowerCase() +
                    "%", categoriesId, paid, pageable);
            return events.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        LocalDateTime startMoment = LocalDateTime.parse(rangeStart, df);
        LocalDateTime endMoment = LocalDateTime.parse(rangeEnd, df);
        if (startMoment.isAfter(endMoment)) {
            throw new ValidationException("Start couldn't be after end");
        }
        events = eventRepository.findEventsByAllCriteries("%" + textAnnotation.toLowerCase() + "%",
                categoriesId, paid, startMoment, endMoment, pageable);

        this.sendStatisticWithManyEvents(request, events);

        return events.stream()
                .map(this::mapToResponse)
                .map(this::countRequests)
                .map(this::getStat)
                .sorted(Comparator.comparing(EventDtoResponse::getViews))
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoResponse getEvent(Integer eventId, HttpServletRequest request) throws EntityNotFoundException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Event with id " + eventId + " was not found");
        }
        EventDtoResponse eventDtoResponse = this.mapToResponse(event);
        this.countRequests(eventDtoResponse);
        String uri = baseUri + "/stats?end=2041-01-01 00:00:00&unique=true&&uris=/events/" + eventId;
        this.sendStatistic(request, "/events/" + event.getId());
        List<ViewStats> stats = this.getStat(uri);
        List<Event> events = new ArrayList<>();
        events.add(event);
        this.parseViewsForEvent(stats, eventDtoResponse);
        return eventDtoResponse;
    }

    private EventDtoResponse mapToResponse(Event event) {
        return eventMapper.eventToEventDtoResponse(event);
    }

    private void sendStatisticWithManyEvents(HttpServletRequest request, List<Event> events) {
        for (Event event : events) {
            String eventUri = "/events/" + event.getId();
            this.sendStatistic(request, eventUri);
        }
    }

    private void sendStatistic(HttpServletRequest request, String uri) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setUri(uri);
        endpointHitDto.setTimestamp(LocalDateTime.now().format(df));
        String sendUri = baseUri + "/hit";
        client.postRequest(sendUri, endpointHitDto);
    }

    @SneakyThrows
    private List<ViewStats> getStat(String uri) {
        ResponseEntity<Object> response = client.getRequest(uri);
        Object body = response.getBody();
        String json = mapper.writeValueAsString(body);

        List<ViewStats> responseList = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, ViewStats.class));

        return responseList;
    }

    private EventDtoResponse countRequests(EventDtoResponse eventDtoResponse) {
        eventDtoResponse.setConfirmedRequests(requestRepository.countConfirmedRequests(eventDtoResponse.getId()));
        return eventDtoResponse;
    }

    private EventDtoResponse getStat(EventDtoResponse eventDtoResponse) {
        String uri = baseUri + "/stats?end=2041-01-01 00:00:00&unique=true&&uris=/events/" + eventDtoResponse.getId();
        List<ViewStats> list = this.getStat(uri);
        this.parseViewsForEvent(list, eventDtoResponse);
        return eventDtoResponse;
    }

    private void parseViewsForEvent(List<ViewStats> viewStatsList, EventDtoResponse eventDtoResponse) {
        for (ViewStats stats : viewStatsList) {
            eventDtoResponse.setViews(stats.getHits());
        }
    }

}
