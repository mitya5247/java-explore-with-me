package ru.practicum.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventPatchException;
import ru.practicum.model.event.dto.EventDtoResponse;
import ru.practicum.model.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.admin.AdminEventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
public class AdminEventController {

    @Autowired
    AdminEventService service;

    @GetMapping
    public List<EventDtoResponse> get(@RequestParam(required = false) List<Integer> users,
                                      @RequestParam(required = false) List<String> states,
                                      @RequestParam(required = false) List<Integer> categories,
                                      @RequestParam(required = false) String rangeStart,
                                      @RequestParam(required = false) String rangeEnd,
                                      @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        return service.get(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDtoResponse patch(@PathVariable Integer eventId, @RequestBody UpdateEventAdminRequest updateEventAdminRequest) throws EventPatchException, EntityNotFoundException {
        return service.patch(eventId, updateEventAdminRequest);
    }
}
