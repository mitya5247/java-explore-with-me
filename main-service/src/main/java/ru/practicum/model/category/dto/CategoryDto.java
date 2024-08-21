package ru.practicum.model.category.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Validated
@Data
public class CategoryDto {
    @NotBlank(message = "name must not be null")
    @Size(max = 50)
    String name;
}
