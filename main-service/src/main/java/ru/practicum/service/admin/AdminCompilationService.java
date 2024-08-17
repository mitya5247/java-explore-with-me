package ru.practicum.service.admin;

import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.model.compilation.dto.NewCompilationDto;
import ru.practicum.model.compilation.dto.UpdateCompilationDto;

public interface AdminCompilationService {

    public CompilationResponseDto create(NewCompilationDto newCompilationDto);

    public CompilationResponseDto patch(Integer compId, UpdateCompilationDto updateCompilationDto) throws EntityNotFoundException;

    public void delete(Integer compId) throws EntityNotFoundException;
}
