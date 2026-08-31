package ru.yandex.practicum.filmorate.storage.film.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.exception.FilmGenreNotFoundException;
import ru.yandex.practicum.filmorate.model.film.FilmGenre;

import java.util.Collection;

@Component("inDbFilmGenreStorage")
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final FilmGenreRepository filmGenreRepository;

    public FilmGenreDbStorage(FilmGenreRepository filmGenreRepository) {
        this.filmGenreRepository = filmGenreRepository;
    }

    @Override
    public void delete(FilmGenre filmGenre) {
        Long id = filmGenre.getId();
        filmGenreRepository.findByGenreId(id)
                .orElseThrow(() -> new FilmGenreNotFoundException("Жанр фильма не найден. ID: " + id));
        filmGenreRepository.delete(id, false);
    }

    @Override
    public Collection<FilmGenre> findAll() {
        return filmGenreRepository.findAll();
    }

    @Override
    public FilmGenre findFilmGenreById(Long id) {
        return filmGenreRepository.findByGenreId(id)
                .orElseThrow(() -> new FilmGenreNotFoundException("Жанр фильма не найден. ID: " + id));
    }
}
