package ru.practicum.service.priv;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.*;
import ru.practicum.mapper.*;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.PrivateStateAction;
import ru.practicum.model.event.State;
import ru.practicum.model.event.Status;
import ru.practicum.model.event.dto.*;
import ru.practicum.model.request.ParticipationRequest;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.model.user.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PrivateEventServiceImpl implements PrivateEventService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    EventMapper eventMapper;
    @Autowired
    LocationMapper locationMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    RequestMapper requestMapper;

    @Override
    public EventDtoResponse create(Integer userId, EventDto eventDto) throws EntityNotFoundException, EventPatchException {
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(() -> new EntityNotFoundException("Category with id " + eventDto.getCategory() +
                " was not found"));
        Event event = eventMapper.eventDtoToEvent(eventDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId +
                " was not found"));
        event.setInitiator(user);
        event.setCategory(category);
        this.validateTimeEvent(event);
        event = eventRepository.save(event);
        return eventMapper.eventToEventDtoResponse(event);
    }

    @Override
    public List<EventShortDto> getEvents(Integer userId, Integer from, Integer size) throws EntityNotFoundException {
        Pageable pageable = PageRequest.of(from/size, size);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId +
                " was not found"));
        List<EventShortDto> list = eventRepository.findAllByInitiator(user, pageable).stream()
                .map(this::countRequests)
                .map(eventShortDtoDb -> eventMapper.eventShortDbToShort(eventShortDtoDb))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public EventDtoResponse getFullEvent(Integer userId, Integer eventId) throws EntityNotFoundException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        EventDtoResponse eventDtoResponse = eventMapper.eventToEventDtoResponse(event);
        eventDtoResponse.setConfirmedRequests(this.fillConfirmedRequests(eventDtoResponse.getId()));
        return eventDtoResponse;
    }

    @Override
    public EventDtoResponse patchEvent(Integer userId, Integer eventId, UpdateEventUserRequest eventDto) throws EntityNotFoundException, EventPatchException, EventAlreadyPublishedException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        if (event.getState().equals(State.PUBLISHED)) {
            throw new EventAlreadyPublishedException("Event with id " + eventId + " already published " + event.getState() +
                    "and couldn't be changed");
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (eventDto.getEventDate() != null) {
            LocalDateTime moment = LocalDateTime.parse(eventDto.getEventDate(), df);
            if (!moment.isAfter(LocalDateTime.now().plusHours(2))) {
                throw new EventPatchException("Event with id " + eventId + "couldn't be less than 1 hours. Date: " + moment);
            }
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventPatchException("User with id " + userId + "couldn't patch user's event with id " + event.getInitiator().getId());
        }
        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new EventPatchException("Event with id " + eventId + "couldn't be less than 2 hours. Date: " + event.getEventDate());
        }
        this.patchEvent(event, eventDto);
        eventRepository.save(event);
        EventDtoResponse eventDtoResponse = eventMapper.eventToEventDtoResponse(event);
        eventDtoResponse.setConfirmedRequests(this.fillConfirmedRequests(eventDtoResponse.getId()));
        return eventDtoResponse;
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Integer userId, Integer eventId) throws EntityNotFoundException, RequestErrorException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId +
                " was not found"));
        List<ParticipationRequest> participationRequests = requestRepository.findAllByEventAndRequester(event, user);
        return participationRequests.stream()
                .map(this::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult patchStatus(Integer userId, Integer eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest)
            throws RequestErrorException, EntityNotFoundException, ParticipationsLimitOvercomeException { // подтверждение заявки на участие
        try {

            Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
            List<Integer> listIdsRequests = eventRequestStatusUpdateRequest.getRequestIds();

            if (event.getParticipantLimit() == 0) {
                this.confirmRequest(listIdsRequests);
            }
            String status = eventRequestStatusUpdateRequest.getStatus();
            EventRequestStatusUpdateResult updateResult = this.fillStatus(event, listIdsRequests, status);

            return updateResult;
        } catch (NullPointerException | EntityNotFoundException e) {
            throw new RequestErrorException("Event with id " + eventId + " was not found");
        }
    }

    private Event patchEvent(Event event1, UpdateEventUserRequest eventDto2) throws EntityNotFoundException {
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
        if (eventDto2.getStateAction() != null && eventDto2.getStateAction().equals(PrivateStateAction.CANCEL_REVIEW.toString())) {
            event1.setState(State.CANCELED);
        } else  {
            event1.setState(State.PENDING);
            event1.setPublishedOn(LocalDateTime.now());
        }
        return event1;
    }

    private EventShortDtoDb countRequests(EventShortDtoDb eventShortDtoDb) {
//        ConfirmedRequest confirmedRequest = requestRepository.countConfRequests(eventShortDtoDb.getId());
        eventShortDtoDb.setConfirmedRequests(requestRepository.countConfirmedRequests(eventShortDtoDb.getId()));
        return eventShortDtoDb;
    }

    private EventRequestStatusUpdateResult fillStatus(Event event, List<Integer> requestIds, String status) throws EntityNotFoundException, ParticipationsLimitOvercomeException {
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        updateResult.setConfirmedRequests(new ArrayList<>());
        updateResult.setRejectedRequests(new ArrayList<>());
        Status statusEnum = Status.valueOf(status);
        switch (statusEnum) {
            case CONFIRMED:
            for (Integer id : requestIds) {
                int count = requestRepository.countConfirmedRequests(event.getId());
                ParticipationRequest request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request with id " + id +
                        " was not found"));
                if (count < event.getParticipantLimit()) {
                    request.setStatus(Status.CONFIRMED);
                    requestRepository.save(request);
                    updateResult.getConfirmedRequests().add(requestMapper.requestToDto(request));
                } else {
//                    request.setStatus(Status.REJECTED);
//                    requestRepository.save(request);
//                    updateResult.getRejectedRequests().add(requestMapper.requestToDto(request));
                    throw new ParticipationsLimitOvercomeException("limit is overcome");
                }
            }
            break;
            case REJECTED:
                for (Integer id : requestIds) {
                    ParticipationRequest request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request with id " + id +
                            " was not found"));
                    request.setStatus(Status.REJECTED);
                    requestRepository.save(request);
                    updateResult.getRejectedRequests().add(requestMapper.requestToDto(request));
                }
                break;

        }
        return updateResult;
    }

    private ParticipationRequestDto mapRequest(ParticipationRequest request) {
        return requestMapper.requestToDto(request);
    }

    private int fillConfirmedRequests(Integer eventId) {
        return requestRepository.countConfirmedRequests(eventId);
    }

    private void confirmRequest(List<Integer> requestIds) throws EntityNotFoundException {
        for (Integer id : requestIds) {
            ParticipationRequest request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request with id " + id +
                    " was not found"));
            request.setStatus(Status.CONFIRMED);
            requestRepository.save(request);
        }
    }

    private void validateTimeEvent(Event event) throws EventPatchException {
        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new EventPatchException("Event should have date after the 2 hours " + event.getEventDate());
        }
    }
}
