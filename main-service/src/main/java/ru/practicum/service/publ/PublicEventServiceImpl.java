package ru.practicum.service.publ;

import org.springframework.stereotype.Service;
import ru.practicum.model.event.dto.EventDtoResponse;

import java.util.List;

@Service
public class PublicEventServiceImpl implements PublicEventService {

    @Override
    public List<EventDtoResponse> get(String textAnnotation, List<Integer> categoriesId, Boolean paid, String rangeStart,
                                      String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public EventDtoResponse getEvent(Integer eventId) {
        return null;
    }
}
