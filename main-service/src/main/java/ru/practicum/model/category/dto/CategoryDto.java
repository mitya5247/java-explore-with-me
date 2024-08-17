package ru.practicum.model.category.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Data
public class CategoryDto {
    @NotBlank(message = "name must not be null")
    @Size(max = 50)
    String name;
}
