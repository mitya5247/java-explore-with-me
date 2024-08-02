package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@AllArgsConstructor
public class StatServiceImpl implements StatService {

    @Autowired
    private final StatRepository repository;

    @Override
    public EndpointHit createHit(HttpServletRequest request, EndpointHit endpointHit) {
        endpointHit.setIp(request.getRemoteAddr());
        endpointHit.setUri(request.getRequestURI());
        return repository.save(endpointHit);
    }

    @Override
    public List<EndpointHit> getStat(String start, String end, List<String> uris, Boolean unique) {
        return List.of();
    }
}
