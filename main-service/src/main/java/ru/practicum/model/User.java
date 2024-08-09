package ru.practicum.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Validated
public class User {
    Integer id;
    @NotBlank(message = "name must not be null or empty")
    String name;
    @Email
    String email;
}
