package ru.practicum.service.admin;

import ru.practicum.exceptions.EmailAlreadyExistsException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.User;

import java.util.List;

public interface AdminUserService {

    public User create(User user) throws EmailAlreadyExistsException;

    public List<User> get(List<Integer> usersId, Integer from, Integer size);

    public void delete(Integer id) throws EntityNotFoundException;
}
