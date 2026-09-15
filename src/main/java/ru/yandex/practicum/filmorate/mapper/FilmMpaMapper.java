package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.film.FilmMpaDto;
import ru.yandex.practicum.filmorate.model.film.FilmMpa;
import ru.yandex.practicum.filmorate.model.film.FilmMpaEnum;

public class FilmMpaMapper {

    public static FilmMpaDto mapToFilmMpaDto(FilmMpa filmMpa) {
        FilmMpaDto dto = new FilmMpaDto();
        dto.setId(filmMpa.getId());
        dto.setName(FilmMpaEnum.mapToString(filmMpa.getName()));
        return dto;
    }
}
