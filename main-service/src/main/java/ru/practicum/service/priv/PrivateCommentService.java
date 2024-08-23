package ru.practicum.service.priv;

import ru.practicum.exceptions.CommentException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventIsNotPublishedException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.comment.dto.CommentDtoResponse;
import ru.practicum.model.comment.dto.NewCommentDto;
import ru.practicum.model.comment.dto.UpdateCommentDto;

import java.util.List;

public interface PrivateCommentService {

    CommentDtoResponse create(Integer userId, Integer eventId, NewCommentDto newCommentDto) throws EntityNotFoundException, EventIsNotPublishedException;

    CommentDtoResponse patch(Integer userId, Integer eventId, UpdateCommentDto updateCommentDto) throws EntityNotFoundException, CommentException;

    CommentDtoResponse getComment(Integer id) throws EntityNotFoundException;

    List<CommentDtoResponse> get(List<Integer> userId, List<Integer> eventId, String text, String startTime, String endTime) throws ValidationException;

    void delete(Integer id) throws EntityNotFoundException;
}
