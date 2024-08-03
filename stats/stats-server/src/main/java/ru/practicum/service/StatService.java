package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    public String createHit(HttpServletRequest request, EndpointHitDto endpointHit);

    public List<ViewStats> getStat(String start, String end, List<String> uris, Boolean unique);
}
