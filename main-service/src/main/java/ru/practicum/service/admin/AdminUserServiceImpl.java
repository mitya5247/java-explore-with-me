package ru.practicum.service.admin;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EmailAlreadyExistsException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.model.user.User;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    UserRepository repository;

    @Override
    public User create(User user) throws EmailAlreadyExistsException {
        try {
            return repository.save(user);
        } catch (ConstraintViolationException e) {
            throw new EmailAlreadyExistsException(e.getMessage());
        }
    }

    @Override
    public List<User> get(List<Integer> usersId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (usersId != null) {
            return repository.findAllById(usersId);
        }
        return repository.findAll(pageable).toList();
    }

    @Override
    public void delete(Integer id) throws EntityNotFoundException {
        User user = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id +
                " was not found"));
        repository.delete(user);
    }
}
