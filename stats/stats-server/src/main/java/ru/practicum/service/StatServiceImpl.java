package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.Mapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
@Service
public class StatServiceImpl implements StatService {

    private final StatRepository repository;

    @Override
    public String createHit(HttpServletRequest request, EndpointHitDto endpointHitDto) {
        endpointHitDto.setIp(request.getRemoteAddr());
        EndpointHit endpointHit = Mapper.convertToEndpointHit(endpointHitDto);
        this.validateTimestamp(endpointHit.getTimestamp());
        repository.save(endpointHit);
        return "Информация сохранена";
    }

    @Override
    public List<ViewStats> getStat(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startMoment = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endMoment = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.validateTimeBounds(startMoment, endMoment);
        List<ViewStats> viewStatsList;
        if (uris == null && unique.equals(false)) {
            viewStatsList = repository.findRequestWithNullUris(startMoment, endMoment);
            return viewStatsList;
        }
        if (unique.equals(true)) {
            viewStatsList = repository.findUniqueRequest(startMoment, endMoment, uris);
        } else {
            viewStatsList = repository.findRequest(startMoment, endMoment, uris);
        }
        return viewStatsList;
    }

    private void validateTimestamp(LocalDateTime time) {
        if (time.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("timestamp must not be in future");
        }
        return;
    }

    private void validateTimeBounds(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("end must not be earlier than start");
        }
        return;
    }
}
