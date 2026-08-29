package ru.yandex.practicum.filmorate.mapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.FilmGenre;
import ru.yandex.practicum.filmorate.model.film.FilmGenreEnum;
import ru.yandex.practicum.filmorate.model.film.FilmMpaEnum;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FilmMapper {

    private static FilmDbStorage filmDbStorage;

    public FilmMapper(FilmDbStorage filmDbStorage) {
        FilmMapper.filmDbStorage = filmDbStorage;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setLikedFilm(film.getLikedFilm());
        if (!(film.getGenres() == null || film.getGenres().isEmpty())) {
            dto.setGenres(film.getGenres().stream()
                    .map(g -> new FilmGenreDto(g.getId(), FilmGenreEnum.fromRussia(g.getName()))).toList());
        }
        if (film.getMpa() != null) {
            dto.setMpa(new FilmMpaDto(film.getMpa().getId(), FilmMpaEnum.mapToString(film.getMpa().getName())));
        }

        return dto;
    }

    public static Film mapToFilmFromNewFilmRequest(NewFilmRequest newFilmRequest) {
        Film film = new Film();
        film.setName(newFilmRequest.getName());
        film.setDescription(newFilmRequest.getDescription());
        film.setReleaseDate(newFilmRequest.getReleaseDate());
        film.setDuration(newFilmRequest.getDuration());

        List<FilmGenreDto> dto = newFilmRequest.getGenres();
        if (!(dto == null || dto.isEmpty())) {
            film.setGenres(mapListFilmGenreFromListFilmGenreDto(dto));
        }

        if (newFilmRequest.getMpa() != null) {
            film.setMpa(filmDbStorage.findFilmMpaById(newFilmRequest.getMpa().getId()));
        }
        return film;
    }

    public static Film mapToFilmFromUpdateFilmRequest(UpdateFilmRequest updateFilmRequest) {
        Film film = new Film();
        film.setId(updateFilmRequest.getId());
        film.setName(updateFilmRequest.getName());
        film.setDescription(updateFilmRequest.getDescription());
        film.setReleaseDate(updateFilmRequest.getReleaseDate());
        film.setDuration(updateFilmRequest.getDuration());
        if (updateFilmRequest.hasGenres()) {
            film.setGenres(mapListFilmGenreFromListFilmGenreDto(updateFilmRequest.getGenres()));
        }
        if (updateFilmRequest.hasMpa()) {
            film.setMpa(filmDbStorage.findFilmMpaById(updateFilmRequest.getMpa().getId()));
        }

        return film;
    }

    public static UpdateFilmRequest mapToUpdateFilmRequest(Film film) {
        UpdateFilmRequest updateFilmRequest = new UpdateFilmRequest();
        updateFilmRequest.setId(film.getId());
        updateFilmRequest.setName(film.getName());
        updateFilmRequest.setDescription(film.getDescription());
        updateFilmRequest.setReleaseDate(film.getReleaseDate());
        updateFilmRequest.setDuration(film.getDuration());

        List<FilmGenre> genre = film.getGenres();
        if (!(genre == null || genre.isEmpty())) {
            updateFilmRequest.setGenres(film.getGenres().stream()
                    .map(g -> new FilmGenreDto(g.getId(), FilmGenreEnum.fromRussia(g.getName())))
                    .toList());
        }

        if (film.getMpa() != null) {
            updateFilmRequest.setMpa(new FilmMpaDto(film.getMpa().getId(), FilmMpaEnum.mapToString(film.getMpa().getName())));
        }

        return updateFilmRequest;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest updateFilmRequest) {
        if (updateFilmRequest.hasId()) {
            film.setId(updateFilmRequest.getId());
        }
        if (updateFilmRequest.hasName()) {
            film.setName(updateFilmRequest.getName());
        }
        if (updateFilmRequest.hasDescription()) {
            film.setDescription(updateFilmRequest.getDescription());
        }
        if (updateFilmRequest.hasReleaseDate()) {
            film.setReleaseDate(updateFilmRequest.getReleaseDate());
        }
        if (updateFilmRequest.hasDuration()) {
            film.setDuration(updateFilmRequest.getDuration());
        }
        if (updateFilmRequest.hasGenres()) {
            film.setGenres(mapListFilmGenreFromListFilmGenreDto(updateFilmRequest.getGenres()));
        }
        if (updateFilmRequest.hasMpa()) {
            film.setMpa(filmDbStorage.findFilmMpaById(updateFilmRequest.getMpa().getId()));
        }

        return film;
    }

    public static List<FilmGenre> mapListFilmGenreFromListFilmGenreDto(List<FilmGenreDto> dto) {
        List<FilmGenre> genres = new ArrayList<>();
        dto.forEach(g -> genres.add(filmDbStorage.findFilmGenreById(g.getId())));
        return genres;
    }
}
