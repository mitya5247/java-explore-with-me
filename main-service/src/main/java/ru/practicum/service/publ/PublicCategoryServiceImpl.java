package ru.practicum.service.publ;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {

    @Autowired
    CategoryRepository repository;

    @Override
    public List<Category> get(Integer from, Integer size) {
        Pageable page = PageRequest.of(from/size, size);
        return repository.findAll(page).toList();
    }

    @Override
    public Category getCategory(Integer id) throws EntityNotFoundException {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category with id " + id +
                " was not found"));
    }
}
