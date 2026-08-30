package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage inMemoryFilmStorage;
    FilmService filmService;

    public FilmController(FilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return inMemoryFilmStorage.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return inMemoryFilmStorage.update(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return inMemoryFilmStorage.findAll();
    }

    @GetMapping("/")
    public Film noneId() {
        log.trace("Передан Get запрос получение фильма по id без id");
        throw new ValidationUserException("Id должен быть указан.");
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.trace("Передан Get запрос получение фильма по id={}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping({"//like/{userId}", "/{id}/like/", "//like/"})
    public Film noneIdOFFilmIdOrUserIdInPutMapping() {
        log.trace("Передан Put запрос на лайк фильма пользователем без id");
        throw new ValidationUserException("Id фильма и id пользователя должны быть указаны.");
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        log.trace("Передан Put запрос на лайк фильма с id={} пользователем с id={}", id, userId);
        return filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping({"//like/{userId}", "/{id}/like/", "//like/"})
    public Film noneIdOFFilmIdOrUserIdInDeleteMapping() {
        log.trace("Передан Delete запрос на удаление лайка из фильма пользователем без id");
        throw new ValidationUserException("Id фильма и id пользователя должны быть указаны.");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        log.trace("Передан Delete запрос на удаление лайка из фильма с id={} пользователем с id={}", id, userId);
        return filmService.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getFirstFewFilms(@RequestParam(defaultValue = "10") int count) {
        log.trace("Передан Get запрос на получение первых {} фильмов с наибольшим рейтингом", count);
        return filmService.getPopularFilms(count);
    }
}
