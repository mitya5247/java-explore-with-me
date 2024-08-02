package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EndpointHit;
import ru.practicum.service.StatServiceImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class StatController {

    @Autowired
    private final StatServiceImpl service;

    @PostMapping("/hit")
    public EndpointHit createHit(HttpServletRequest request, @Valid EndpointHit endpointHit) {
        return service.createHit(request, endpointHit);
    }

    @GetMapping("/stats")
    public List<EndpointHit> getStat(@RequestParam String start, @RequestParam String end, @RequestParam List<String> uris,
                                     @RequestParam(defaultValue = "false") Boolean unique) {
        return service.getStat(start, end, uris, unique);
    }
}
