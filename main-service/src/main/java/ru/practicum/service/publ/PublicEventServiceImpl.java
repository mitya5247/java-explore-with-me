package ru.practicum.service.publ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
//        if (paid == null) {
//            paid = false;
//        }
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

        HttpStatus status = this.sendStatistic(request);
        String uri = baseUri + "/stats?end=2041-01-01 00:00:00&uris=/events";
        ResponseEntity<Object> response = client.getRequest(baseUri + "/stats?end=2041-01-01 00:00:00&uris=/events");
        List<ViewStats> list = this.getStat(uri);

        return events.stream()
                .map(this::mapToResponse)
                .map(this::countRequests)
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoResponse getEvent(Integer eventId, HttpServletRequest request) throws EntityNotFoundException, JsonProcessingException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        this.sendStatistic(request);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Event with id " + eventId + " was not found");
        }
        EventDtoResponse eventDtoResponse = this.mapToResponse(event);
        this.countRequests(eventDtoResponse);
        String uri = baseUri + "/stats?end=2041-01-01 00:00:00&uris=/events/" + eventId;
        List<ViewStats> stats = this.getStat(uri);
        return eventDtoResponse;
    }

    private EventDtoResponse mapToResponse(Event event) {
        return eventMapper.eventToEventDtoResponse(event);
    }

    private HttpStatus sendStatistic(HttpServletRequest request) {

        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setUri(request.getRequestURI());
        endpointHitDto.setTimestamp(LocalDateTime.now().format(df));

        String uri = baseUri + "/hit";
        ResponseEntity<Object> response = client.postRequest(uri, endpointHitDto);
        return response.getStatusCode();
    }

    private List<ViewStats> getStat(String uri) throws JsonProcessingException {
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
}
