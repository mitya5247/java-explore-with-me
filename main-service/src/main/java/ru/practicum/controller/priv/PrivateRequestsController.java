package ru.practicum.controller.priv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.service.priv.PrivateRequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class PrivateRequestsController {

    @Autowired
    PrivateRequestService service;

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> get(@PathVariable Integer userId) {
        return service.get(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto create(@PathVariable Integer userId, @RequestParam Integer eventId) throws EntityNotFoundException {
        return service.create(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto patch(@PathVariable Integer userId, @PathVariable Integer requestId, @RequestBody ParticipationRequestDto requestDto) {
        return service.patch(userId, requestId, requestDto);
    }
}
