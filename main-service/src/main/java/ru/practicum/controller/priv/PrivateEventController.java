package ru.practicum.controller.priv;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventPatchException;
import ru.practicum.exceptions.RequestErrorException;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.*;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.service.priv.PrivateEventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PrivateEventController {

    @Autowired
    PrivateEventService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/events")
    public EventDtoResponse create(@PathVariable Integer userId, @Valid @RequestBody EventDto eventDto) throws EntityNotFoundException {
        return service.create(userId, eventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDtoResponse patch(@PathVariable Integer userId, @PathVariable Integer eventId,
                                  @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) throws EventPatchException, EntityNotFoundException {
        return service.patchEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDtoResponse getFullEvent(@PathVariable Integer userId, @PathVariable Integer eventId) throws EntityNotFoundException {
        return service.getFullEvent(userId, eventId);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable Integer userId, @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) throws EntityNotFoundException {
        return service.getEvents(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Integer userId, @PathVariable Integer eventId) throws RequestErrorException, EntityNotFoundException {
        return service.getRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult patchStatus(@PathVariable Integer userId, @PathVariable Integer eventId,
                                                      @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest)
                                                        throws RequestErrorException, EntityNotFoundException {
        return service.patchStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
