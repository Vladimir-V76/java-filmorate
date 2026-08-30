package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.film.FilmGenreDto;
import ru.yandex.practicum.filmorate.dto.film.FilmMpaDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.*;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmStorageTests {

    @Autowired
    private FilmDbStorage filmStorage;
    private final NewFilmRequest newFilm = new NewFilmRequest();

    FilmGenre setFilmGenre(Long id, FilmGenreEnum name) {
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setId(id);
        filmGenre.setName(name);
        return filmGenre;
    }

    FilmMpa setFilmMpa(Long id, FilmMpaEnum name) {
        FilmMpa filmMpa = new FilmMpa();
        filmMpa.setId(id);
        filmMpa.setName(name);
        return filmMpa;
    }

    void fillNewFilmRequest() {
        newFilm.setName("newFilmName");
        newFilm.setDescription("NewFilmDescription");
        newFilm.setReleaseDate(LocalDate.parse("2000-01-01"));
        newFilm.setDuration(100);
        newFilm.setGenres(List.of(
                new FilmGenreDto(1L, "COMEDY"), new FilmGenreDto(2L, "DRAMA"))
        );
        newFilm.setMpa(new FilmMpaDto(1L, "G"));
    }

    @Test
    void shouldBe1AfterFindFilmByIdThereIdIs1() {
        Film film = filmStorage.findFilmById(1L);
        assertThat(film.getId()).isEqualTo(1L);
    }

    @Test
    void shouldFilmNotFoundExceptionFromFindUserByIdThereIdIsNotExist() {
        assertThatThrownBy(() -> filmStorage.findFilmById(10L))
                .isInstanceOf(FilmNotFoundException.class)
                .hasMessageContaining("Фильм не найден. ID: 10");
    }

    @Test
    void testCreateMustMeetRequiredRequirementsOnCorrectFields() {
        //должен вернуться id=4 при создании нового film и поля отправленного film соответствовать полям созданного
        fillNewFilmRequest();
        filmStorage.create(FilmMapper.mapToFilmFromNewFilmRequest(newFilm));

        Film film = filmStorage.findFilmById(4L);
        assertThat(film.getId()).isEqualTo(4L);
        assertThat(film.getName()).isEqualTo("newFilmName");
        assertThat(film.getDescription()).isEqualTo("NewFilmDescription");
        assertThat(film.getReleaseDate()).isEqualTo("2000-01-01");
        assertThat(film.getDuration()).isEqualTo(100);
        assertThat(film.getGenres())
                .isEqualTo(List.of(setFilmGenre(1L, FilmGenreEnum.COMEDY),
                        setFilmGenre(2L, FilmGenreEnum.DRAMA)
                ));
        assertThat(film.getMpa()).isEqualTo(setFilmMpa(1L, FilmMpaEnum.G));
    }

    @Test
    void testUpdateMeetRequiredRequirementsOnCorrectFields() {
        fillNewFilmRequest();
        filmStorage.create(FilmMapper.mapToFilmFromNewFilmRequest(newFilm));

        //должны поля отправленного updateFilm соответствовать полям полученного film
        UpdateFilmRequest updateFilm = new UpdateFilmRequest();
        updateFilm.setId(4L);
        updateFilm.setName("updateFilmName");
        updateFilm.setDescription("updateFilmDescription");
        updateFilm.setReleaseDate(LocalDate.parse("2002-02-02"));
        updateFilm.setDuration(200);
        updateFilm.setGenres(List.of(new FilmGenreDto(3L, "CARTOON")));
        updateFilm.setMpa(new FilmMpaDto(2L, "PG"));

        filmStorage.update(FilmMapper.mapToFilmFromUpdateFilmRequest(updateFilm));

        Film film = filmStorage.findFilmById(4L);
        assertThat(film.getId()).isEqualTo(4L);
        assertThat(film.getName()).isEqualTo("updateFilmName");
        assertThat(film.getDescription()).isEqualTo("updateFilmDescription");
        assertThat(film.getReleaseDate()).isEqualTo("2002-02-02");
        assertThat(film.getDuration()).isEqualTo(200);
        assertThat(film.getGenres())
                .isEqualTo(List.of(setFilmGenre(3L, FilmGenreEnum.CARTOON)));
        assertThat(film.getMpa()).isEqualTo(setFilmMpa(2L, FilmMpaEnum.PG));
    }

    @Test
    public void testFindAllShouldBeListOf3Items() {
        List<Film> films = new ArrayList<>(filmStorage.findAll());
        assertThat(films.size()).isEqualTo(3);

        assertThat(films.getFirst().getId()).isEqualTo(1L);
        assertThat(films.getFirst().getName()).isEqualTo("Film 1");
        assertThat(films.getFirst().getDescription()).isEqualTo("Description film 1");
        assertThat(films.getFirst().getReleaseDate()).isEqualTo("2001-01-01");
        assertThat(films.getFirst().getDuration()).isEqualTo(100);
        assertThat(films.getFirst().getLikedFilm()).isEqualTo(Set.of(1L, 2L, 3L));
        assertThat(films.getFirst().getGenres())
                .isEqualTo(List.of(
                        setFilmGenre(1L, FilmGenreEnum.COMEDY),
                        setFilmGenre(3L, FilmGenreEnum.CARTOON),
                        setFilmGenre(4L, FilmGenreEnum.THRILLER)
                ));
        assertThat(films.getFirst().getMpa()).isEqualTo(setFilmMpa(1L, FilmMpaEnum.G));

        assertThat(films.get(1).getId()).isEqualTo(2L);
        assertThat(films.get(1).getName()).isEqualTo("Film 2");
        assertThat(films.get(1).getDescription()).isEqualTo("Description film 2");
        assertThat(films.get(1).getReleaseDate()).isEqualTo("2001-02-01");
        assertThat(films.get(1).getDuration()).isEqualTo(102);
        assertThat(films.get(1).getLikedFilm()).isEqualTo(Set.of(2L, 3L));
        assertThat(films.get(1).getGenres())
                .isEqualTo(List.of(
                        setFilmGenre(2L, FilmGenreEnum.DRAMA),
                        setFilmGenre(5L, FilmGenreEnum.DOCUMENTARY)
                ));
        assertThat(films.get(1).getMpa()).isEqualTo(setFilmMpa(2L, FilmMpaEnum.PG));

        assertThat(films.get(2).getId()).isEqualTo(3L);
        assertThat(films.get(2).getName()).isEqualTo("Film 3");
        assertThat(films.get(2).getDescription()).isEqualTo("Description film 3");
        assertThat(films.get(2).getReleaseDate()).isEqualTo("2001-03-01");
        assertThat(films.get(2).getDuration()).isEqualTo(103);
        assertThat(films.get(2).getLikedFilm()).isEqualTo(Set.of(1L));
        assertThat(films.get(2).getGenres())
                .isEqualTo(List.of(setFilmGenre(6L, FilmGenreEnum.ACTION)));
        assertThat(films.get(2).getMpa()).isEqualTo(setFilmMpa(3L, FilmMpaEnum.PG_13));
    }

    @Test
    void shouldBy1ToFindFilmGenreById() {
        FilmGenre filmGenre = filmStorage.findFilmGenreById(1L);
        assertThat(filmGenre.getId()).isEqualTo(1L);
    }

    @Test
    void shouldFilmGenreNotFoundExceptionFromFindFilmGenreByIdThereIdIsNotExist() {
        assertThatThrownBy(() -> filmStorage.findFilmGenreById(10L))
                .isInstanceOf(FilmGenreNotFoundException.class)
                .hasMessageContaining("Указан не корректный id жанра: 10");
    }

    @Test
    void shouldBy1ToFindFilmMpaById() {
        FilmMpa filmMpa = filmStorage.findFilmMpaById(1L);
        assertThat(filmMpa.getId()).isEqualTo(1L);
    }

    @Test
    void shouldMpaNotFoundExceptionFromFindFilmMpaByIdThereIdIsNotExist() {
        assertThatThrownBy(() -> filmStorage.findFilmMpaById(10L))
                .isInstanceOf(MpaNotFoundException.class)
                .hasMessageContaining("Указан не корректный id рейтинга: 10");
    }
}

