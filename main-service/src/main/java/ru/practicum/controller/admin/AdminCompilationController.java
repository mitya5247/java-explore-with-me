package ru.practicum.controller.admin;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Constants;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.model.compilation.dto.NewCompilationDto;
import ru.practicum.model.compilation.dto.UpdateCompilationDto;
import ru.practicum.service.admin.AdminCompilationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@AllArgsConstructor
public class AdminCompilationController {

    @Autowired
    AdminCompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto create(@Valid @RequestBody NewCompilationDto compilationDto) {
        return service.create(compilationDto);
    }

    @PatchMapping(Constants.COMPILATION_PATH_ID)
    public CompilationResponseDto patch(@PathVariable(name = "comp-id") Integer compId,
                                        @Valid @RequestBody UpdateCompilationDto updateCompilationDto) throws EntityNotFoundException {
        return service.patch(compId, updateCompilationDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(Constants.COMPILATION_PATH_ID)
    public void delete(@PathVariable(name = "comp-id") Integer compId) throws EntityNotFoundException {
        service.delete(compId);
    }
}
