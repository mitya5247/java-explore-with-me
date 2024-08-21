package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStats;
import ru.practicum.exception.InvalidDateTimeException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface StatService {

    public String createHit(HttpServletRequest request, EndpointHitDto endpointHit) throws InvalidDateTimeException;

    public List<ViewStats> getStat(String start, String end, List<String> uris, Boolean unique) throws InvalidDateTimeException;
}
