package ru.yandex.practicum.filmorate.storage.film.mpa;

import ru.yandex.practicum.filmorate.model.film.FilmMpa;

import java.util.Collection;

public interface FilmMpaStorage {

    void delete(FilmMpa filmMpa);

    Collection<FilmMpa> findAll();

    FilmMpa findFilmMpaById(Long id);
}
