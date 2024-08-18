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
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.Sort;
import ru.practicum.model.event.State;
import ru.practicum.model.event.dto.EventDtoResponse;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
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
    ObjectMapper mapper = new ObjectMapper();
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public PublicEventServiceImpl(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper,
                                  @Value("${stats-uri}") String baseUri, StatsClient client) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.client = client;
        this.baseUri = baseUri;
    }

    @Override
    public List<EventDtoResponse> get(String textAnnotation, List<Integer> categoriesId, Boolean paid, String rangeStart,
                                      String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                      HttpServletRequest request) throws JsonProcessingException {
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
        events = eventRepository.findEventsByAllCriteries("%" + textAnnotation.toLowerCase() + "%",
                categoriesId, paid, startMoment, endMoment, pageable);

        HttpStatus status = this.sendStatistic(request);
        String uri = baseUri + "/stats?end=2041-01-01 00:00:00&uris=/events";
//        ResponseEntity<Object> response = client.getRequest(baseUri + "/stats?end=2041-01-01 00:00:00&uris=/events");
        List<ViewStats> list = this.getStat(uri);

        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoResponse getEvent(Integer eventId, HttpServletRequest request) throws EntityNotFoundException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        this.sendStatistic(request);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Event with id " + eventId + " was not found");
        }
        return this.mapToResponse(event);
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
        return client.postRequest(uri, endpointHitDto).getStatusCode();
    }

    private List<ViewStats> getStat(String uri) throws JsonProcessingException {
        ResponseEntity<Object> response = client.getRequest(uri);
        response.getBody();
//        List<ViewStats> list = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, ViewStats.class));
        return new ArrayList<>();
    }
}
