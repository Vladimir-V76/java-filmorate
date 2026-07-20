package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    FilmStorage filmStorage;
    UserService userService;
    private final Comparator<Film> filmLikeComparator = Comparator.comparing(s -> s.getLikedFilm().size());

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film getFilmById(Long id) {
        validationId(id);
        return findFilmById(id);
    }

    public Film addLikeFilm(Long id, Long userId) {
        validationId(id);
        userService.validationId(userId);
        Film film = findFilmById(id);
        User user = userService.findUserById(userId);
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
        Film film = findFilmById(id);
        User user = userService.findUserById(userId);
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

    public Film findFilmById(Long id) {
        Optional<Film> searchFilm = filmStorage.findAll().stream()
                .filter(f -> Objects.equals(f.getId(), id))
                .findFirst();
        if (searchFilm.isEmpty()) {
            throw new FilmNotFoundException("Фильм с id=" + id + " не найден");
        }
        log.trace("Фильм с id={} успешно найден", id);
        return searchFilm.get();
    }

}
