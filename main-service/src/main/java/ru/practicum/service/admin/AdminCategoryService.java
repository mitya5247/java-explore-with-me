package ru.practicum.service.admin;

import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.dto.CategoryDto;

public interface AdminCategoryService {

    public Category create(CategoryDto categoryDto);

    public Category patch(Integer id, CategoryDto categoryDto) throws EntityNotFoundException;

    public void delete(Integer id) throws EntityNotFoundException;
}
