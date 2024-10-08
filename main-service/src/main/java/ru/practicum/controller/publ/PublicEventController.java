package ru.practicum.controller.publ;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Constants;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.event.dto.EventDtoResponse;
import ru.practicum.service.publ.PublicEventService;

import java.util.List;

@RestController
@RequestMapping("/events")
public class PublicEventController {

    @Autowired
    PublicEventService service;

    @GetMapping
    public List<EventDtoResponse> get(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Integer> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false) String rangeStart,
                                      @RequestParam(required = false) String rangeEnd,
                                      @RequestParam(required = false) Boolean onlyAvailable,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      HttpServletRequest request) throws JsonProcessingException, ValidationException {
        return service.get(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping(Constants.EVENT_PATH_ID)
    public EventDtoResponse getEvent(@PathVariable(name = "event-id") Integer eventId, HttpServletRequest request) throws EntityNotFoundException {
        return service.getEvent(eventId, request);
    }
}
