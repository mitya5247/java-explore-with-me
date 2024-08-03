package ru.practicum;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * Hello world!
 */
@Getter
@Setter
@Validated
public class EndpointHitDto {
    String app;
    String uri;
    String ip;
    String timestamp;
}
