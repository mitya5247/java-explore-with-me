package ru.practicum.controller.priv;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Constants;
import ru.practicum.exceptions.CommentException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventIsNotPublishedException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.comment.dto.CommentDtoResponse;
import ru.practicum.model.comment.dto.NewCommentDto;
import ru.practicum.model.comment.dto.UpdateCommentDto;
import ru.practicum.service.priv.PrivateCommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class PrivateCommentController {

    @Autowired
    PrivateCommentService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(Constants.USER_PATH_ID + Constants.EVENT_PATH + Constants.EVENT_PATH_ID)
    public CommentDtoResponse create(@PathVariable(name = "user-id") Integer userId,
                                     @PathVariable(name = "event-id") Integer eventId, @Valid @RequestBody
                                     NewCommentDto newCommentDto) throws
                                     EventIsNotPublishedException, EntityNotFoundException {
        return service.create(userId, eventId, newCommentDto);
    }

    @PatchMapping(Constants.USER_PATH_ID + "/comment" + Constants.COMMENT_ID)
    public CommentDtoResponse patch(@PathVariable(name = "user-id") Integer userId,
                                    @PathVariable(name = "comment-id") Integer commentId, @Valid @RequestBody
                                    UpdateCommentDto updateCommentDto) throws
                                    EntityNotFoundException, CommentException {
        return service.patch(userId, commentId, updateCommentDto);
    }

    @GetMapping(Constants.COMMENT_ID)
    public CommentDtoResponse getComment(@PathVariable(name = "comment-id") Integer commentId) throws EntityNotFoundException {
        return service.getComment(commentId);
    }

    @GetMapping
    public List<CommentDtoResponse> get(@RequestParam(required = false) List<Integer> userId,
                                        @RequestParam(required = false) List<Integer> eventId,
                                        @RequestParam(required = false) String text,
                                        @RequestParam(required = false) String startTime,
                                        @RequestParam(required = false) String endTime) throws EntityNotFoundException, ValidationException {
        return service.get(userId, eventId, text, startTime, endTime);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(Constants.COMMENT_ID)
    public void delete(@PathVariable(name = "comment-id") Integer commentId) throws EntityNotFoundException {
        service.delete(commentId);
    }
}
