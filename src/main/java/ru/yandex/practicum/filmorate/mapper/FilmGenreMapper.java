package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmGenreDto;
import ru.yandex.practicum.filmorate.model.film.FilmGenre;
import ru.yandex.practicum.filmorate.model.film.FilmGenreEnum;

@Service
public class FilmGenreMapper {

    public static FilmGenreDto mapToFilmGenreDto(FilmGenre filmGenre) {
        FilmGenreDto dto = new FilmGenreDto();
        dto.setId(filmGenre.getId());
        dto.setName(FilmGenreEnum.fromRussia(filmGenre.getName()));
        return dto;
    }
}
