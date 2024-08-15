package ru.practicum.service.publ;

import ru.practicum.model.event.dto.EventDtoResponse;

import java.util.List;

public interface PublicEventService {

    public List<EventDtoResponse> get(String textAnnotation, List<Integer> categoriesId, Boolean paid, String rangeStart,
                                      String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size);

    public EventDtoResponse getEvent(Integer eventId);
}
