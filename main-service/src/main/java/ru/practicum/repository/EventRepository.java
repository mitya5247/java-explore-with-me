package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.State;
import ru.practicum.model.event.dto.EventShortDtoDb;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

//    @Query(value = "select new ru.practicum.model.event.dto.EventShortDto(e.annotation, e.category, " +
//            "e.eventDate, e.id, e.initiator, e.paid, e.title) from Event as e where e.initiator.id = :initiatorId")
//    List<EventShortDto> findAllByInitiator(User user, Pageable pageable); // добавидть сортировку и добавить проекцию на этот класс для преобр в объект

    @Query(value = "select new ru.practicum.model.event.dto.EventShortDtoDb(e.annotation, e.category, 0, e.eventDate, e.id, " +
            "e.initiator, e.paid, e.title, 0) from Event as e where e.initiator = :initiator")
    List<EventShortDtoDb> findAllByInitiator(@Param("initiator") User user1, Pageable pageable); // добавидть сортировку и добавить проекцию на этот класс для преобр в объект

    @Query(value = "select e from Event e where e.initiator.id IN :usersId AND e.state IN :states AND e.category.id IN " +
            ":categoriesId AND e.eventDate BETWEEN :startTime AND :endTime")
    List<Event> findEventByUsersAndStateAndCategoryBetween(@Param("usersId") List<Integer> usersId, @Param("states") List<State> states,
                        @Param("categoriesId") List<Integer> categoriesId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime, Pageable pageable);  // admin - запрос сервиса со временем

    @Query(value = "select e from Event e where e.initiator.id IN :usersId AND e.state IN :states and e.category.id IN " +
            ":categoriesId")
    List<Event> findEventByUsersAndStateAndCategory(@Param("usersId") List<Integer> usersId,
                                                    @Param("states") List<State> states,
                                                    @Param("categoriesId") List<Integer> categoriesId, Pageable pageable); // admin - запрос сервиса без времени


    @Query(value = "select e from Event e where LOWER(e.annotation) like LOWER(:text) AND e.category.id IN :categoriesId " +
            "AND e.paid = :paid AND e.eventDate BETWEEN :start AND :end")
    List<Event> findEventsByAllCriteries(@Param("text") String textAnnotation, @Param("categoriesId") List<Integer> categoriesId,
                                         @Param("paid") Boolean paid, @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end); // сортировка в сервисе , public - со временем


    @Query(value = "select e from Event e where LOWER(e.annotation) like LOWER(:text) AND e.category.id IN :categoriesId " +
            "AND e.paid = :paid")
    List<Event> findEventsByAllCriteriesWithoutTime(@Param("text") String textAnnotation, @Param("categoriesId") List<Integer> categoriesId,
                                         @Param("paid") Boolean paid); // public - без времени

}