package ru.practicum.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.model.comment.Comment;

import java.util.List;

public class CommentSpecification {

    private Specification<Comment> findByUsers(List<Integer> usersId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(Comment_.AUTHOR)).value(usersId);
    }
}
