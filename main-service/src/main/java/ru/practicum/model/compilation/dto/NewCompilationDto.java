package ru.practicum.model.compilation.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {

    List<Integer> events;
    Boolean pinned;
    @Size(min = 1, max = 50)
    @NotBlank
    String title;
}
