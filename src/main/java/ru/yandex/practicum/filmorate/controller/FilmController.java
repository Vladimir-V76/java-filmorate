package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody NewFilmRequest film) {
        return filmService.create(film);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest film) {
        return filmService.update(film);
    }

    @GetMapping
    public Collection<FilmDto> findAll() { return filmService.findAll(); }

    @GetMapping("/")
    public FilmDto noneId() {
        log.trace("Передан Get запрос получение фильма по id без id");
        throw new ValidationUserException("Id должен быть указан.");
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(
            @NotNull(message = "Id должен быть указан")
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id) {
        log.trace("Передан Get запрос получение фильма по id={}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping({"//like/{userId}", "/{id}/like/", "//like/"})
    public FilmDto noneIdOfFilmIdOrUserIdInPutMapping() {
        log.trace("Передан Put запрос на лайк фильма пользователем без id");
        throw new ValidationUserException("Id фильма и id пользователя должны быть указаны.");
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto addLikeFilm(
            @NotNull(message = "Id должен быть указан")
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id,

            @NotNull(message = "UserId должен быть указан")
            @Min(value = 1, message = "UserId должно быть числом положительным")
            @PathVariable Long userId) {
        log.trace("Передан Put запрос на лайк фильма с id={} пользователем с id={}", id, userId);
        return filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping({"//like/{userId}", "/{id}/like/", "//like/"})
    public FilmDto noneIdOfFilmIdOrUserIdInDeleteMapping() {
        log.trace("Передан Delete запрос на удаление лайка из фильма пользователем без id");
        throw new ValidationUserException("Id фильма и id пользователя должны быть указаны.");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto deleteLikeFilm(
            @NotNull(message = "Id должен быть указан")
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id,

            @NotNull(message = "UserId должен быть указан")
            @Min(value = 1, message = "UserId должно быть числом положительным")
            @PathVariable Long userId) {
        log.trace("Передан Delete запрос на удаление лайка из фильма с id={} пользователем с id={}", id, userId);
        return filmService.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getFirstFewFilms(@RequestParam(defaultValue = "10") int count) {
        log.trace("Передан Get запрос на получение первых {} фильмов с наибольшим рейтингом", count);
        return filmService.getPopularFilms(count);
    }
}
