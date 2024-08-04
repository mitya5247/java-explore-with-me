package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class StatController {

    @Autowired
    private final StatServiceImpl service;

    @PostMapping("/hit")
    public String createHit(HttpServletRequest request, @RequestBody EndpointHitDto endpointHit) {
        return service.createHit(request, endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStat(@RequestParam(required = false) String start, @RequestParam(required = false) String end, @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false", required = false) Boolean unique) {
        return service.getStat(start, end, uris, unique);
    }
}
