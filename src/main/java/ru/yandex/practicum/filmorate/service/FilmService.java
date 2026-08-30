package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Comparator<Film> filmLikeComparator = Comparator.comparing(s -> s.getLikedFilm().size());

    public FilmService(
            @Qualifier("inDbFilmStorage") FilmStorage filmStorage,
            @Qualifier("inDbUserStorage") UserStorage userStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<FilmDto> findAll() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto update(UpdateFilmRequest updateFilm) {
        Film film = FilmMapper.mapToFilmFromUpdateFilmRequest(updateFilm);
        checkFilmValidation(film);
        return FilmMapper.mapToFilmDto(filmStorage.update(film));
    }

    public FilmDto create(NewFilmRequest createFilm) {
        Film film = FilmMapper.mapToFilmFromNewFilmRequest(createFilm);
        checkFilmValidation(film);
        return FilmMapper.mapToFilmDto(filmStorage.create(film));
    }

    public FilmDto getFilmById(Long id) {
        return FilmMapper.mapToFilmDto(filmStorage.findFilmById(id));
    }

    public FilmDto addLikeFilm(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        if (!film.getLikedFilm().add(user.getId())) {
            String message = "Пользователь с id=%s уже лайкнул фильм с id=%d";
            throw new DuplicateItemException(String.format(message, userId, id));
        }
        log.trace("Фильм с id={} пользователь с id={} успешно лайкнул", id, userId);
        film = filmStorage.update(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto deleteLikeFilm(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        if (!film.getLikedFilm().remove(user.getId())) {
            String message = "Фильм с id=%s пользователь с id=%d не лайкал.";
            throw new NotFoundItemException(String.format(message, id, userId));
        }
        log.trace("Из фильма с id={} лайк пользователя с id={} успешно удален", id, userId);
        film = filmStorage.update(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDto> getPopularFilms(int count) {
        if (count <= 0) {
            String message = "Указано количество фильмов: %s. Должно быть число положительное.";
            throw new ValidationFilmException(String.format(message, count));
        }
        return filmStorage.findAll().stream()
                .sorted(filmLikeComparator.reversed())
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    private static void checkFilmValidation(Film film) {
        String filmValidation = "Ok";

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            filmValidation = "Максимальная длина описания — 200 символов";
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            filmValidation = "Дата релиза — не раньше 28 декабря 1895 года";
        }
        if (film.getDuration() <= 0) {
            filmValidation = "Продолжительность фильма должна быть положительным числом";
        }
        if (!filmValidation.equals("Ok")) {
            throw new ValidationFilmException(filmValidation);
        }
    }

}
