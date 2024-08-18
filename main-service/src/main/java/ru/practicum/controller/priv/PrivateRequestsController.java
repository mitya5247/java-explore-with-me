package ru.practicum.controller.priv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ParticipationsLimitOvercomeException;
import ru.practicum.exceptions.RequestErrorException;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.service.priv.PrivateRequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class PrivateRequestsController {

    @Autowired
    PrivateRequestService service;

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> get(@PathVariable Integer userId) throws EntityNotFoundException {
        return service.get(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto create(@PathVariable Integer userId, @RequestParam Integer eventId) throws
            EntityNotFoundException, ParticipationsLimitOvercomeException, RequestErrorException {
        return service.create(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto patch(@PathVariable Integer userId, @PathVariable Integer requestId) throws EntityNotFoundException {
        return service.patch(userId, requestId);
    }
}
