package ru.practicum.repository.specification;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Comment.class)
public abstract class Comment_ {

    public static volatile SingularAttribute<Comment, User> author;
    public static volatile SingularAttribute<Comment, Event> event;
    public static volatile SingularAttribute<Comment, String> text;
    public static volatile SingularAttribute<Comment, LocalDateTime> date;

    public static final String AUTHOR = "author";
    public static final String TEXT = "text";
    public static final String EVENT = "event";
    public static final String DATE = "date";


}