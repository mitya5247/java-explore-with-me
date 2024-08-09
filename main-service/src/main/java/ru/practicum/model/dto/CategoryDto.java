package ru.practicum.model.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Data
public class CategoryDto {
    @NotBlank(message = "name must not be null")
    String name;
}
