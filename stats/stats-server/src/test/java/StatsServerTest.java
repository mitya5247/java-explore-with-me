import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.service.StatService;
import ru.practicum.service.StatServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

@SpringBootTest(
        classes = {StatServiceImpl.class, EndpointHit.class},
        properties = "db.name=test"
)
@EnableJpaRepositories("ru.practicum")
@EntityScan("ru.practicum.model")
@EnableAutoConfiguration
public class StatsServerTest {

    private final StatService service;

    EndpointHitDto endpointHitDto;

    @Autowired
    EntityManager em;

    @Autowired
    HttpServletRequest request;

    @Autowired
    public StatsServerTest(StatServiceImpl service) {
        this.service = service;
    }

    @BeforeEach
    public void initContext() {
        endpointHitDto = new EndpointHitDto();
        endpointHitDto.setIp("0:0:0:123");
        endpointHitDto.setApp("myApp");
        endpointHitDto.setUri("/my-uri");
        endpointHitDto.setTimestamp("2021-12-31 18:21:23");
    }

    @Test
    public void saveEndpoint() {
        endpointHitDto.setApp("myApp1");
        service.createHit(request, endpointHitDto);

        TypedQuery<EndpointHit> query = em.createQuery("select e from EndpointHit e where e.app = :app", EndpointHit.class)
                .setParameter("app", endpointHitDto.getApp());

        EndpointHit endpointHit = query.getSingleResult();

        Assertions.assertEquals(endpointHitDto.getApp(), endpointHit.getApp());
        Assertions.assertEquals(endpointHitDto.getUri(), endpointHit.getUri());
    }

    @Test
    public void saveEndpointWithNullApp() {
        endpointHitDto.setApp(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> service.createHit(request, endpointHitDto));
    }

    @Test
    public void saveEndpointWithNullUri() {
        endpointHitDto.setUri(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> service.createHit(request, endpointHitDto));
    }

}
