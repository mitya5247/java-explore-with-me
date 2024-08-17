package ru.practicum.service.priv;

import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventPatchException;
import ru.practicum.exceptions.RequestErrorException;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.*;
import ru.practicum.model.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {

    public EventDtoResponse create(Integer userId, EventDto eventDto) throws EntityNotFoundException;

    public List<EventShortDto> getEvents(Integer userId, Integer from, Integer size) throws EntityNotFoundException;

    public EventDtoResponse getFullEvent(Integer userId, Integer eventId) throws EntityNotFoundException;

    public EventDtoResponse patchEvent(Integer userId, Integer eventId, UpdateEventUserRequest eventDto) throws EntityNotFoundException, EventPatchException;

    public List<ParticipationRequestDto> getRequests(Integer userId, Integer eventId) throws EntityNotFoundException, RequestErrorException;

    public EventRequestStatusUpdateResult patchStatus(Integer userId, Integer eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) throws RequestErrorException, EntityNotFoundException;

}
