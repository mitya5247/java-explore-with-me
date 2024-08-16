package ru.practicum.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class StatController {

    @Autowired
    private final StatServiceImpl service;
    ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/hit")
    public String createHit(HttpServletRequest request, @RequestBody EndpointHitDto endpointHit) throws JsonProcessingException {
        String response = service.createHit(request, endpointHit);
        String json = mapper.writeValueAsString(response);
        return json;
//        return service.createHit(request, endpointHit);
    }

    @GetMapping("/stats")
    public String getStat(@RequestParam(defaultValue = "2021-01-01 00:00:00") String start,
                                   @RequestParam(defaultValue = "2021-01-01 00:00:00") String end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false", required = false) Boolean unique) throws JsonProcessingException {
//        String json = mapper.writeValueAsString(service.getStat(start, end, uris, unique));
        List<ViewStats> list = service.getStat(start, end, uris, unique);
        String json = mapper.writeValueAsString(list);
        return json;
    }
}
