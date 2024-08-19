import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatService;
import ru.practicum.service.StatServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(
        classes = {StatServiceImpl.class, EndpointHit.class},
        properties = "db.name=test"
)
@EnableJpaRepositories("ru.practicum")
@EntityScan("ru.practicum.model")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
    public void getUnuniqueStatistic() {
        String start = "2020-12-31 13:27:45";
        String end = "2030-03-21 00:00:00";
        List<String> uris = new ArrayList<>();
        uris.add(endpointHitDto.getUri());

        EndpointHitDto endpointHitDto2 = new EndpointHitDto();
        endpointHitDto2.setIp("0:0:0:123");
        endpointHitDto2.setApp("myApp");
        endpointHitDto2.setUri("/my-uri");
        endpointHitDto2.setTimestamp("2021-03-31 18:21:23");

        service.createHit(request, endpointHitDto);
        service.createHit(request, endpointHitDto2);

        Long hits = service.getStat(start, end, uris, false).get(0).getHits();
        Assertions.assertEquals(2L, hits);
    }

    @Test
    public void getUniqueStatistic() {
        String start = "2020-12-31 13:27:45";
        String end = "2030-03-21 00:00:00";
        List<String> uris = new ArrayList<>();
        uris.add(endpointHitDto.getUri());

        EndpointHitDto endpointHitDto2 = new EndpointHitDto();
        endpointHitDto2.setIp("0:0:0:123");
        endpointHitDto2.setApp("myApp");
        endpointHitDto2.setUri("/my-uri");
        endpointHitDto2.setTimestamp("2021-03-31 18:21:23");

        service.createHit(request, endpointHitDto);
        service.createHit(request, endpointHitDto2);

        Long hits = service.getStat(start, end, uris, true).get(0).getHits();
        Assertions.assertEquals(1L, hits);
    }

    @Test
    public void getStatisticOutOfBounds() {
        String start = "2027-12-31 13:27:45";
        String end = "2030-03-21 00:00:00";
        List<String> uris = new ArrayList<>();
        uris.add(endpointHitDto.getUri());

        EndpointHitDto endpointHitDto2 = new EndpointHitDto();
        endpointHitDto2.setIp("0:0:0:123");
        endpointHitDto2.setApp("myApp");
        endpointHitDto2.setUri("/my-uri");
        endpointHitDto2.setTimestamp("2021-03-31 18:21:23");

        service.createHit(request, endpointHitDto);
        service.createHit(request, endpointHitDto2);

        List<ViewStats> viewStatsList = service.getStat(start, end, uris, true);
        Assertions.assertEquals(0, viewStatsList.size());
    }

    @Test
    public void getStatisticWithInvalidTime() {
        String start = "2030-12-31 13:27:45";
        String end = "2028-03-21 00:00:00";
        List<String> uris = new ArrayList<>();
        uris.add(endpointHitDto.getUri());

        EndpointHitDto endpointHitDto2 = new EndpointHitDto();
        endpointHitDto2.setIp("0:0:0:123");
        endpointHitDto2.setApp("myApp");
        endpointHitDto2.setUri("/my-uri");
        endpointHitDto2.setTimestamp("2021-03-31 18:21:23");

        service.createHit(request, endpointHitDto);
        service.createHit(request, endpointHitDto2);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.getStat(start, end, uris, true));
    }
}
