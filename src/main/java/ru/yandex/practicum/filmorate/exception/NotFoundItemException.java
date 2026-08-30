package ru.yandex.practicum.filmorate.exception;

public class NotFoundItemException extends RuntimeException {
    public NotFoundItemException(String message) {
        super(message);
    }
}
