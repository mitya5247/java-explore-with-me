package ru.practicum.service.priv;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.Status;
import ru.practicum.model.request.ParticipationRequest;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    EventMapper eventMapper;
    @Autowired
    RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> get(Integer userId) throws EntityNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId +
                " was not found"));
        List<ParticipationRequest> participationRequests = requestRepository.findAllByRequester(user);
        return participationRequests.stream()
                .map(this::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto create(Integer userId, Integer eventId) throws EntityNotFoundException {
        ParticipationRequest request = new ParticipationRequest();
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId +
                " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        request.setRequester(user);
        request.setEvent(event);
        request.setStatus(Status.PENDING);
        if (event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
        }
        request = requestRepository.save(request);
        return requestMapper.requestToDto(request);
    }

    @Override
    public ParticipationRequestDto patch(Integer userId, Integer requestId) throws EntityNotFoundException {
        ParticipationRequest request = requestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException("Request with id " + requestId +
                " was not found"));
        if (request.getRequester().getId().equals(userId)) {
            request.setStatus(Status.CANCELED);
        }
        return requestMapper.requestToDto(request);
    }

    private ParticipationRequestDto mapRequest(ParticipationRequest request) {
        return requestMapper.requestToDto(request);
    }
}
