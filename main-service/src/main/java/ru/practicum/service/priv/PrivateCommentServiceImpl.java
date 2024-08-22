package ru.practicum.service.priv;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.CommentException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventIsNotPublishedException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.dto.CommentDtoResponse;
import ru.practicum.model.comment.dto.NewCommentDto;
import ru.practicum.model.comment.dto.UpdateCommentDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.State;
import ru.practicum.model.user.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    CommentMapper commentMapper;

    @Override
    public CommentDtoResponse create(Integer userId, Integer eventId, NewCommentDto newCommentDto) throws EntityNotFoundException, EventIsNotPublishedException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId +
                " was not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EventIsNotPublishedException("Event with id " + eventId + "is not published");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId +
                " was not found"));
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setTimestamp(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return commentMapper.convertToResponse(comment);
    }

    @Override
    public CommentDtoResponse patch(Integer userId, Integer commentId, UpdateCommentDto updateCommentDto) throws EntityNotFoundException, CommentException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment with id " + commentId +
                " was not found"));
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new CommentException("couldn't patch comment as you not author");
        }
        return null;
    }

    @Override
    public CommentDtoResponse getComment(Integer id) throws EntityNotFoundException {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment with id " + id +
                " was not found"));
        return commentMapper.convertToResponse(comment);
    }

    @Override
    public List<CommentDtoResponse> get(List<Integer> userId, List<Integer> eventId, String text, String startTime, String endTime) {
        return List.of();
    }

    @Override
    public void delete(Integer id) throws EntityNotFoundException {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment with id " + id +
                " was not found"));
        commentRepository.delete(comment);
    }
}
