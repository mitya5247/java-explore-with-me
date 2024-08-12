package ru.practicum.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "users")
@Validated
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @NotBlank(message = "name must not be null or empty")
    @Column(name = "name")
    @Size(min = 2, max = 250)
    String name;
    @NotBlank
    @Email
    @Column(name = "email")
    @Size(min = 6, max = 254)
    String email;
}
