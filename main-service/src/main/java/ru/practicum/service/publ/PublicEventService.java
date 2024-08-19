package ru.practicum.service.publ;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.event.dto.EventDtoResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicEventService {

    public List<EventDtoResponse> get(String textAnnotation, List<Integer> categoriesId, Boolean paid, String rangeStart,
                                      String rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                      Integer size, HttpServletRequest request) throws JsonProcessingException, ValidationException;

    public EventDtoResponse getEvent(Integer eventId, HttpServletRequest request) throws EntityNotFoundException;
}
