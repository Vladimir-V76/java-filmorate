package ru.yandex.practicum.filmorate.storage.film.genre;

import ru.yandex.practicum.filmorate.model.film.FilmGenre;

import java.util.Collection;

public interface FilmGenreStorage {


    void delete(FilmGenre filmGenre);

    Collection<FilmGenre> findAll();

    FilmGenre findFilmGenreById(Long id);
}
