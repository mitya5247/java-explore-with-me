package ru.practicum.service;

import ru.practicum.model.EndpointHit;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StatService {

    public EndpointHit createHit(HttpServletRequest request, EndpointHit endpointHit);

    public List<EndpointHit> getStat(String start, String end, List<String> uris, Boolean unique);
}
