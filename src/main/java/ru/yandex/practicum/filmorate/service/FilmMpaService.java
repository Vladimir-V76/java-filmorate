package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmMpaDto;
import ru.yandex.practicum.filmorate.mapper.FilmMpaMapper;
import ru.yandex.practicum.filmorate.storage.film.mpa.FilmMpaStorage;

import java.util.Collection;

@Service
public class FilmMpaService {
    private final FilmMpaStorage filmMpaStorage;

    public FilmMpaService(@Qualifier("inDbFilmMpaStorage") FilmMpaStorage filmMpaStorage) {
        this.filmMpaStorage = filmMpaStorage;
    }

    public Collection<FilmMpaDto> findAll() {
        return filmMpaStorage.findAll().stream()
                .map(FilmMpaMapper::mapToFilmMpaDto)
                .toList();
    }

    public FilmMpaDto getFilmMpaById(Long id) {
        return FilmMpaMapper.mapToFilmMpaDto(filmMpaStorage.findFilmMpaById(id));
    }
}
