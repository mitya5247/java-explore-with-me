package ru.practicum.controller.priv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Constants;
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

    @GetMapping(Constants.USER_PATH_ID + Constants.REQUESTS_PATH)
    public List<ParticipationRequestDto> get(@PathVariable(name = "user-id") Integer userId) throws EntityNotFoundException {
        return service.get(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(Constants.USER_PATH_ID + Constants.REQUESTS_PATH)
    public ParticipationRequestDto create(@PathVariable(name = "user-id") Integer userId,
                                          @RequestParam Integer eventId) throws
            EntityNotFoundException, ParticipationsLimitOvercomeException, RequestErrorException {
        return service.create(userId, eventId);
    }

    @PatchMapping(Constants.USER_PATH_ID + Constants.REQUESTS_PATH + Constants.REQUEST_PATH_ID + "/cancel")
    public ParticipationRequestDto patch(@PathVariable(name = "user-id") Integer userId,
                                         @PathVariable(name = "request-id") Integer requestId) throws EntityNotFoundException {
        return service.patch(userId, requestId);
    }
}
