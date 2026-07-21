package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    FilmStorage filmStorage;
    UserService userService;
    UserStorage userStorage;
    private final Comparator<Film> filmLikeComparator = Comparator.comparing(s -> s.getLikedFilm().size());

    public FilmService(FilmStorage filmStorage, UserService userService, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.userStorage = userStorage;
    }

    public Film getFilmById(Long id) {
        validationId(id);
        return filmStorage.findFilmById(id);
    }

    public Film addLikeFilm(Long id, Long userId) {
        validationId(id);
        userService.validationId(userId);
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        if (!film.getLikedFilm().add(user.getId())) {
            throw new DuplicateItemException("Пользователь с id=" + userId +
                    " уже лайкнул фильм с id=" + id);
        }
        log.trace("Фильм с id={} пользователя с id={} успешно лайкнул", id, userId);
        return film;
    }

    public Film deleteLikeFilm(Long id, Long userId) {
        validationId(id);
        userService.validationId(userId);
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        if (!film.getLikedFilm().remove(user.getId())) {
            throw new NotFoundItemException("Фильм с id=" + id + " пользователь с id=" + userId + " не лайкал");
        }
        log.trace("Из фильма с id={} лайк пользователя с id={} успешно удален", id, userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationFilmException("Указано количество фильмов: " + count +
                    ". Должно быть число положительное");
        }
        return filmStorage.findAll().stream()
                .sorted(filmLikeComparator.reversed())
                .limit(count)
                .toList();
    }

    public void validationId(Long id) {
        if (id <= 0) {
            throw new ValidationUserException("Неверный id=" + id + ". Должно быть положительное число.");
        }
    }
}
