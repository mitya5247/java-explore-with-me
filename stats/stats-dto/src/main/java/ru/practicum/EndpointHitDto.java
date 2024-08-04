package ru.practicum;

import lombok.Getter;
import lombok.Setter;

/**
 * Hello world!
 */
@Getter
@Setter
public class EndpointHitDto {
    String app;
    String uri;
    String ip;
    String timestamp;
}
