package ru.practicum.controller.admin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.EmailAlreadyExistsException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.user.User;
import ru.practicum.service.admin.AdminUserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserController {

    @Autowired
    AdminUserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) throws EmailAlreadyExistsException {
        return service.create(user);
    }

    @GetMapping
    public List<User> get(@RequestParam(required = false, name = "ids") List<Integer> usersId, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        return service.get(usersId, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) throws EntityNotFoundException {
        service.delete(id);
    }
}
