package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.event.Event;
import ru.practicum.model.request.ParticipationRequest;
import ru.practicum.model.user.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {

    @Query("select count(p) from ParticipationRequest p where p.event.id = :id AND p.status = 'CONFIRMED'")
    int countConfirmedRequests(@Param("id") Integer eventId); // подсчет уже подтвержденных запросов

    @Query("select count(p) from ParticipationRequest p where p.event.id = :id")
    int countRequests(@Param("id") Integer eventId); // подсчет общего количества заявок на событие

    List<ParticipationRequest> findAllByRequester(User user);

    ParticipationRequest findByRequester(User user);

    List<ParticipationRequest> findAllByEvent(Event event);

}
