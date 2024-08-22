package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.comment.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {


    @Query("select c from Comment c where (c.author IN :users) AND (c.event IN :events) AND LOWER(c.text) like LOWER(:text) " +
            "AND c.timestamp BETWEEN :start AND :end")
    List<Comment> findComments(@Param("users") List<Integer> userId,
                               @Param("events") List<Integer> eventId,
                               @Param("text") String text,
                               @Param("start") String startTime,
                               @Param("end") String endTime);
}
