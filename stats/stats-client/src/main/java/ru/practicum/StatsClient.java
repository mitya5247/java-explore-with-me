package ru.practicum;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.client.RestTemplate;

/**
 * Hello world!
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsClient {
    RestTemplate template;

    public StatsClient(RestTemplate template) {
        this.template = template;
    }
}
