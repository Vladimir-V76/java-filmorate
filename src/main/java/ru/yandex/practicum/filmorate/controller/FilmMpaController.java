package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.FilmMpaDto;
import ru.yandex.practicum.filmorate.exception.ValidationFilmMpaException;
import ru.yandex.practicum.filmorate.service.FilmMpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@Validated
public class FilmMpaController {
    private final FilmMpaService filmMpaService;

    public FilmMpaController(FilmMpaService filmMpaService) {
        this.filmMpaService = filmMpaService;
    }

    @GetMapping
    public Collection<FilmMpaDto> findAll() {
        return filmMpaService.findAll();
    }

    @GetMapping("/")
    public FilmMpaDto noneId() {
        log.trace("Передан Get запрос получение рейтинга по id без id");
        throw new ValidationFilmMpaException("Id должен быть указан.");
    }

    @GetMapping("/{id}")
    public FilmMpaDto getFilmById(
            @NotNull(message = "Id должен быть указан")
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id) {
        log.trace("Передан Get запрос получение рейтинга по id={}", id);
        return filmMpaService.getFilmMpaById(id);
    }
}
