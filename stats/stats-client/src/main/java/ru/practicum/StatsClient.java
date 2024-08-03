package ru.practicum;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
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

    private <T> ResponseEntity<Object> prepareRequest(String uri, T body,
                                                HttpMethod method) {
        HttpEntity<Object> entity = new HttpEntity<>(body, this.defaultHeaders());

        ResponseEntity<Object> response = template.exchange(uri, method, entity, Object.class);

        return response;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
