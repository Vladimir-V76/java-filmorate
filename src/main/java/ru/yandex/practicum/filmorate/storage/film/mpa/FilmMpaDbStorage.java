package ru.yandex.practicum.filmorate.storage.film.mpa;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmMpaRepository;
import ru.yandex.practicum.filmorate.exception.FilmGenreNotFoundException;
import ru.yandex.practicum.filmorate.model.film.FilmMpa;

import java.util.Collection;

@Component("inDbFilmMpaStorage")
public class FilmMpaDbStorage implements FilmMpaStorage{

    public FilmMpaRepository filmMpaRepository;

    public FilmMpaDbStorage(FilmMpaRepository filmMpaRepository) {
        this.filmMpaRepository = filmMpaRepository;
    }

    @Override
    public FilmMpa create(FilmMpa filmMpa) {
        return null;
    }

    @Override
    public FilmMpa update(FilmMpa filmMpa) {
        return null;
    }

    @Override
    public void delete(FilmMpa filmMpa) {

    }

    @Override
    public Collection<FilmMpa> findAll() {
        return filmMpaRepository.findAll();
    }

    @Override
    public FilmMpa findFilmMpaById(Long id) {
        return filmMpaRepository.findById(id)
                .orElseThrow(() -> new FilmGenreNotFoundException("Рейтинг фильма не найден. ID: " + id));
    }
}
