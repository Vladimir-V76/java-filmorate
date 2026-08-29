package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationFilmException;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Primary
@Component ("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private static final Map<Long, Film> films = new TreeMap<>();

    public static void clear() {
        films.clear();
    }

    @Override
    public void delete(Film film) {

    }

    @Override
    public Film create(Film film) {
        try {
            checkFilmValidation(film);
        } catch (ValidationFilmException e) {
            log.warn("Ошибка валидации при создании фильма: {}", e.getMessage());
            throw e;
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с id={} успешно добавлен", film.getId());
        return film;
    }

    private static long getNextId() {
        long currentMaxId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private static void checkFilmValidation(Film film) {
        String filmValidation = "Ok";
        if (film.getDescription() == null || film.getReleaseDate() == null) {
            filmValidation = "Запрос не полный, отсутствует часть информации";
        }
        if (film.getName() == null || film.getName().isBlank()) {
            filmValidation = "Название не должно быть пустым";
        }
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

    @Override
    public Film update(Film film) {
        Film currectedFilm;
        try {
            if (film.getId() == null) {
                throw new ValidationFilmException("Id должен быть указан");
            }
            Optional<Film> searchFilm = films.values().stream()
                    .filter(f -> Objects.equals(f.getId(), film.getId()))
                    .findFirst();
            if (searchFilm.isEmpty()) {
                throw new FilmNotFoundException("Фильм с id=" + film.getId() + " не найден");
            }
            checkFilmValidation(film);
            currectedFilm = searchFilm.get();
        } catch (ValidationFilmException | FilmNotFoundException e) {
            log.warn("Ошибка валидации при изменение данных фильма: {}", e.getMessage());
            throw e;
        }
        currectedFilm.setName(film.getName());
        currectedFilm.setDuration(film.getDuration());
        currectedFilm.setDescription(film.getDescription());
        currectedFilm.setReleaseDate(film.getReleaseDate());
        log.info("Данные фильма с id={} успешно изменены", film.getId());
        films.put(currectedFilm.getId(), currectedFilm);
        return currectedFilm;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findFilmById(Long id) {
        if (films.containsKey(id)) {
            log.trace("Фильм с id={} успешно найден", id);
            return films.get(id);
        } else {
            throw new FilmNotFoundException("Фильм с id=" + id + " не найден");
        }
    }
}
