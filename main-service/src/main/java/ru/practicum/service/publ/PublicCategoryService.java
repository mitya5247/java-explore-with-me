package ru.practicum.service.publ;

import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.category.Category;

import java.util.List;

public interface PublicCategoryService {

    public List<Category> get(Integer from, Integer size);

    public Category getCategory(Integer id) throws EntityNotFoundException;
}
