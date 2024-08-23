package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.comment.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {


    @Query("select c from Comment c where (c.author.id IN :users) AND (c.event.id IN :events) AND (LOWER(c.text) like LOWER(:text)) " +
            "AND c.timestamp BETWEEN :start AND :end")
    List<Comment> findComments(@Param("users") List<Integer> userId,
                               @Param("events") List<Integer> eventId,
                               @Param("text") String text,
                               @Param("start") LocalDateTime startTime,
                               @Param("end") LocalDateTime endTime);

    @Query("select distinct c.author.id from Comment c")
    List<Integer> findUsersId();

    @Query("select distinct c.event.id from Comment c")
    List<Integer> findEventsId();

}
