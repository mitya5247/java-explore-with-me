package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.event.Event;
import ru.practicum.model.request.ParticipationRequest;
import ru.practicum.model.request.dto.ConfirmedRequest;
import ru.practicum.model.user.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {

//    @Query("select count(p) from ParticipationRequest p where p.event.id = :id AND p.status = 'CONFIRMED'")
//    int countConfirmedRequests(@Param("id") Integer eventId); // первый вариант

    @Query("select count(p) from ParticipationRequest p where p.event.id = :id")
    int countConfirmedRequests(@Param("id") Integer eventId);

    List<ParticipationRequest> findAllByEventAndRequester(Event event, User user);

    List<ParticipationRequest> findAllByRequester(User user);

    @Query("select new ru.practicum.model.request.dto.ConfirmedRequest(p.event.id, count(p)) from ParticipationRequest as p where " +
            "p.event.id = :id AND p.status = 'CONFIRMED'")
    ConfirmedRequest countConfRequests(@Param("id") Integer eventId);

    ParticipationRequest findByRequester(User user);

}
