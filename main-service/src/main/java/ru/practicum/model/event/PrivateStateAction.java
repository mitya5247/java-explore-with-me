package ru.practicum.model.event;

public enum PrivateStateAction { // если событию нужна модерация, то отправка на ревью или отмена
    CANCEL_REVIEW, SEND_TO_REVIEW
}
