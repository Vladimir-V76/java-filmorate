package ru.yandex.practicum.filmorate.storage.film.mpa;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmMpaRepository;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.film.FilmMpa;

import java.util.Collection;

@Component("inDbFilmMpaStorage")
public class FilmMpaDbStorage implements FilmMpaStorage {

    public FilmMpaRepository filmMpaRepository;

    public FilmMpaDbStorage(FilmMpaRepository filmMpaRepository) {
        this.filmMpaRepository = filmMpaRepository;
    }

    @Override
    public void delete(FilmMpa filmMpa) {
        Long id = filmMpa.getId();
        filmMpaRepository.findById(id)
                .orElseThrow(() -> new MpaNotFoundException("Рейтинг фильма не найден. ID: " + id));
        filmMpaRepository.delete(id);
    }

    @Override
    public Collection<FilmMpa> findAll() {
        return filmMpaRepository.findAll();
    }

    @Override
    public FilmMpa findFilmMpaById(Long id) {
        return filmMpaRepository.findById(id)
                .orElseThrow(() -> new MpaNotFoundException("Рейтинг фильма не найден. ID: " + id));
    }
}
