package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.FilmGenreDto;
import ru.yandex.practicum.filmorate.exception.ValidationFilmGenreException;
import ru.yandex.practicum.filmorate.service.FilmGenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@Validated
public class FilmGenreController {
    private final FilmGenreService filmGenreService;

    public FilmGenreController(FilmGenreService filmGenreService) {
        this.filmGenreService = filmGenreService;
    }

    @GetMapping
    public Collection<FilmGenreDto> findAll() { return filmGenreService.findAll(); }

    @GetMapping("/")
    public FilmGenreDto noneId() {
        log.trace("Передан Get запрос получение жанра по id без id");
        throw new ValidationFilmGenreException("Id должен быть указан.");
    }

    @GetMapping("/{id}")
    public FilmGenreDto getFilmGenreById(
            @NotNull(message = "Id должен быть указан")
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id) {
        log.trace("Передан Get запрос получение жанра по id={}", id);
        return filmGenreService.getFilmGenreById(id);
    }
}
