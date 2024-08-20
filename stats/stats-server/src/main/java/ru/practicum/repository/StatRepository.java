package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("select new ru.practicum.ViewStats(e.app, e.uri, count(distinct e.ip) as hits) from EndpointHit as e where e.uri IN :uris AND e.timestamp " +
            "BETWEEN :start AND :end group by e.uri, e.app order by hits desc")
    public List<ViewStats> findUniqueRequest(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query("select new ru.practicum.ViewStats(e.app, e.uri, count(e.uri) as hits) from EndpointHit as e where e.uri IN :uris AND e.timestamp " +
            "BETWEEN :start AND :end group by e.uri, e.app order by hits desc")
    public List<ViewStats> findRequest(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,@Param("uris") List<String> uris);

    @Query("select new ru.practicum.ViewStats(e.app, e.uri, count(e.uri) as hits) from EndpointHit as e where e.timestamp " +
            "BETWEEN :start AND :end group by e.uri, e.app order by hits desc")
    public List<ViewStats> findRequestWithNullUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select distinct new ru.practicum.ViewStats(e.app, e.uri, count(distinct e.uri) as hits) from EndpointHit as e where e.timestamp " +
            "BETWEEN :start AND :end group by e.uri, e.app order by hits desc")
    public List<ViewStats> findDistinctRequestWithNullUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
