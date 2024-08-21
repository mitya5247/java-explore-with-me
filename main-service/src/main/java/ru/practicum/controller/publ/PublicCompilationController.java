package ru.practicum.controller.publ;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Constants;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.service.publ.PublicCompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@AllArgsConstructor
public class PublicCompilationController {

    @Autowired
    PublicCompilationService service;

    @GetMapping
    public List<CompilationResponseDto> get(@RequestParam(required = false) Boolean pinned,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return service.get(pinned, from, size);
    }

    @GetMapping(Constants.COMPILATION_PATH_ID)
    public CompilationResponseDto getCompilation(@PathVariable(name = "comp-id") Integer compId) throws EntityNotFoundException {
        return service.getCompilation(compId);
    }
}
