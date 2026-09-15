package ru.yandex.practicum.filmorate.exception;

public class FilmGenreNotFoundException extends RuntimeException {
    public FilmGenreNotFoundException(String message) {
        super(message);
    }
}
