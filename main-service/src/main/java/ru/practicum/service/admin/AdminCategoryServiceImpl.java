package ru.practicum.service.admin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.repository.CategoryRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    @Autowired
    CategoryRepository repository;
    @Autowired
    CategoryMapper mapper;


    @Override
    public Category create(CategoryDto categoryDto) {
        Category category = mapper.categoryDtoToCategory(categoryDto);
        return repository.save(category);
    }

    @Override
    public Category patch(Integer id, CategoryDto categoryDto) throws EntityNotFoundException {
        Category category = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category with id " + id +
                " was not found"));
        category.setName(categoryDto.getName());
        return repository.save(category);
    }

    @Override
    public void delete(Integer id) throws ru.practicum.exceptions.EntityNotFoundException {
        Category category = repository.findById(id).orElseThrow(() -> new ru.practicum.exceptions.EntityNotFoundException("Category with id " + id +
                " was not found"));
        repository.delete(category);
    }
}
