package ru.practicum.controller.publ;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Constants;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.service.publ.PublicCategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PublicCategoryController {

    @Autowired
    PublicCategoryService service;

    @GetMapping
    public List<Category> get(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        return service.get(from, size);
    }

    @GetMapping(Constants.ID)
    public Category getCategory(@PathVariable Integer id) throws EntityNotFoundException {
        return service.getCategory(id);
    }
}
