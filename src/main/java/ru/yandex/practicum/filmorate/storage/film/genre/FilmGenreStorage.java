package ru.yandex.practicum.filmorate.storage.film.genre;

import ru.yandex.practicum.filmorate.model.film.FilmGenre;

import java.util.Collection;

public interface FilmGenreStorage {

    FilmGenre create(FilmGenre filmGenre);

    FilmGenre update(FilmGenre filmGenre);

    void delete(FilmGenre filmGenre);

    Collection<FilmGenre> findAll();

    FilmGenre findFilmGenreById(Long id);
}
