package ru.practicum.service.priv;

import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ParticipationsLimitOvercomeException;
import ru.practicum.exceptions.RequestErrorException;
import ru.practicum.model.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {

    List<ParticipationRequestDto> get(Integer userId) throws EntityNotFoundException;

    ParticipationRequestDto create(Integer userId, Integer eventId) throws EntityNotFoundException, ParticipationsLimitOvercomeException, RequestErrorException;

    ParticipationRequestDto patch(Integer userId, Integer requestId) throws EntityNotFoundException;
}
