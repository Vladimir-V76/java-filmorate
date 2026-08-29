package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.dal.FilmLikedUserRepository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.FilmMpaRepository;
import ru.yandex.practicum.filmorate.dto.film.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.FilmGenre;
import ru.yandex.practicum.filmorate.model.film.FilmMpa;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component("inDbFilmStorage")
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final FilmLikedUserRepository filmLikedUserRepository;
    private final FilmMpaRepository filmMpaRepository;

    public FilmDbStorage(FilmRepository filmRepository,
                         FilmGenreRepository filmGenreRepository,
                         FilmLikedUserRepository filmLikedUserRepository,
                         FilmMpaRepository filmMpaRepository
    ) {
        this.filmGenreRepository = filmGenreRepository;
        this.filmRepository = filmRepository;
        this.filmLikedUserRepository = filmLikedUserRepository;
        this.filmMpaRepository = filmMpaRepository;
    }

    @Override
    public Film create(Film film) {
        List<FilmGenre> filmGenres = film.getGenres();
        FilmMpa mpa = film.getMpa();

        if (mpa != null) {
            FilmMpa filmMpa = findFilmMpaById(mpa.getId());
            film = filmRepository.create(film, filmMpa.getId());
        } else {
            film = filmRepository.create(film, 0L);
        }

        if (!(filmGenres == null || filmGenres.isEmpty())) {
            long filmId = film.getId();
            filmGenres.forEach(n -> {
                FilmGenre fg = findFilmGenreById(n.getId());
                filmGenreRepository.create(fg, filmId);
            });
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        UpdateFilmRequest updateFilm = FilmMapper.mapToUpdateFilmRequest(film);
        long filmId = film.getId();
        Film updatedFilm = filmRepository.findById(filmId)
                .map(f -> FilmMapper.updateFilmFields(f, updateFilm))
                .orElseThrow(() -> new FilmNotFoundException("Фильм не найден. ID: " + filmId));
        List<FilmGenre> filmGenres = film.getGenres();
        FilmMpa mpa = film.getMpa();

        if (mpa != null) {
            FilmMpa filmMpa = findFilmMpaById(mpa.getId());
            updatedFilm = filmRepository.update(updatedFilm, filmMpa.getId());
        } else {
            updatedFilm = filmRepository.update(updatedFilm, 0L);
        }

        if (!(filmGenres == null || filmGenres.isEmpty())) {
            filmGenreRepository.delete(filmId);
            filmGenres.forEach(n -> {
                FilmGenre fg = findFilmGenreById(n.getId());
                filmGenreRepository.create(fg, filmId);
            });
        }
        if (!film.getLikedFilm().isEmpty()) {updateSetFilmLikedUser(film); }
        updatedFilm.setLikedFilm(findSetFilmLikedUserByFilmId(updatedFilm.getId()));
        return updatedFilm;
    }

    @Override
    public void delete(Film film) {

    }

    @Override
    public Collection<Film> findAll() {
        List<Film> films = filmRepository.findAll();
        films.forEach(f -> f.setGenres(findListFilmGenresByFilmId(f.getId())));
        films.forEach(f -> f.setLikedFilm(findSetFilmLikedUserByFilmId(f.getId())));
        return films;
    }

    @Override
    public Film findFilmById(Long filmId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм не найден. ID: " + filmId));
        film.setGenres(findListFilmGenresByFilmId(film.getId()));
        film.setLikedFilm(findSetFilmLikedUserByFilmId(film.getId()));
        return film;
    }

    public List<FilmGenre> findListFilmGenresByFilmId(Long filmId) {
        return filmGenreRepository.findByFilmId(filmId).stream().toList();
    }

    public Set<Long> findSetFilmLikedUserByFilmId(Long filmId) {
        return filmLikedUserRepository.findById(filmId).stream()
                    .map(FilmLikedUser::getUserId)
                    .collect(Collectors.toCollection(HashSet::new));
    }

    public FilmMpa findFilmMpaById(Long id) {
        return filmMpaRepository.findById(id)
                .orElseThrow(() -> new MpaNotFoundException("Указан не корректный id рейтинга: " + id));
    }

    public FilmGenre findFilmGenreById(long id) {
        return filmGenreRepository.findByGenreId(id)
                .orElseThrow(() -> new FilmGenreNotFoundException("Указан не корректный id жанра: " + id));
    }

    public void updateSetFilmLikedUser(Film film) {
        filmLikedUserRepository.delete(film.getId());
        film.getLikedFilm().forEach(l -> filmLikedUserRepository.create(film.getId(), l));
    }
}

