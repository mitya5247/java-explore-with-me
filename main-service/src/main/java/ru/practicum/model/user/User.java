package ru.practicum.model.user;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
