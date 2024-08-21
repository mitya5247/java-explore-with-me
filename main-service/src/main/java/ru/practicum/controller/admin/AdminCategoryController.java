package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Constants;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.service.admin.AdminCategoryService;

@RestController
@RequestMapping("/admin/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class AdminCategoryController {

    @Autowired
    AdminCategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category create(@RequestBody @Valid CategoryDto categoryDto) {
        return service.create(categoryDto);
    }

    @PatchMapping(Constants.ID)
    public Category patch(@PathVariable Integer id, @Valid @RequestBody CategoryDto categoryDto) throws EntityNotFoundException {
        return service.patch(id, categoryDto);
    }


    @DeleteMapping(Constants.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) throws EntityNotFoundException {
        service.delete(id);
    }
}
