package ru.yandex.practicum.filmorate.exception;

public class DuplicateItemException extends RuntimeException {
    public DuplicateItemException(String message) {
        super(message);
    }
}
