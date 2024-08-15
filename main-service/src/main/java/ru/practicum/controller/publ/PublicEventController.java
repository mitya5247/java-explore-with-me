package ru.practicum.controller.publ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.event.dto.EventDtoResponse;
import ru.practicum.service.publ.PublicEventService;

import java.util.List;

@RestController
@RequestMapping("/events")
public class PublicEventController {

    @Autowired
    PublicEventService service;

    @GetMapping
    public List<EventDtoResponse> get(@RequestParam String textAnnotation, @RequestParam List<Integer> categoriesId,
                                      @RequestParam(defaultValue = "false") Boolean paid, @RequestParam String rangeStart,
                                      @RequestParam String rangeEnd, @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam String sort, @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size) {
        return service.get(textAnnotation, categoriesId, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDtoResponse getEvent(@PathVariable Integer eventId) {
        return service.getEvent(eventId);
    }
}
