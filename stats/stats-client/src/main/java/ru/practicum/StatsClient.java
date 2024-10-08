package ru.practicum;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsClient {
    RestTemplate template;

    public StatsClient(RestTemplate template) {
        this.template = template;
    }

    public ResponseEntity<Object> getRequest(String uri) {
        return this.prepareRequest(uri, null, HttpMethod.GET);
    }

    public <T> ResponseEntity<Object> postRequest(String uri, T body) {
        return this.prepareRequest(uri, body, HttpMethod.POST);
    }

    private <T> ResponseEntity<Object> prepareRequest(String uri, T body, HttpMethod method) {
        HttpEntity<Object> entity = new HttpEntity<>(body, this.defaultHeaders());
        return template.exchange(uri, method, entity, Object.class);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
