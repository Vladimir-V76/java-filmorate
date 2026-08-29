package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmGenreDto;
import ru.yandex.practicum.filmorate.mapper.FilmGenreMapper;
import ru.yandex.practicum.filmorate.storage.film.genre.FilmGenreStorage;

import java.util.Collection;

@Service
public class FilmGenreService {

    private final FilmGenreStorage filmGenreStorage;

    public FilmGenreService(@Qualifier("inDbFilmGenreStorage") FilmGenreStorage filmGenreStorage) {
        this.filmGenreStorage = filmGenreStorage;
    }

    public Collection<FilmGenreDto> findAll() {
        return filmGenreStorage.findAll().stream()
                .map(FilmGenreMapper::mapToFilmGenreDto)
                .toList();
    }

    public FilmGenreDto getFilmGenreById(Long id) {
        return FilmGenreMapper.mapToFilmGenreDto(filmGenreStorage.findFilmGenreById(id));
    }

}
