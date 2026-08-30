package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmGenreNotFoundException;
import ru.yandex.practicum.filmorate.model.film.*;
import ru.yandex.practicum.filmorate.storage.film.genre.FilmGenreDbStorage;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmGenreStorageTests {

    @Autowired
    private FilmGenreDbStorage filmGenreStorage;

    @Test
    void shouldBy1ToFindFilmGenreById() {
        FilmGenre filmGenre = filmGenreStorage.findFilmGenreById(1L);
        assertThat(filmGenre.getId()).isEqualTo(1L);
    }

    @Test
    void shouldFilmGenreNotFoundExceptionFromFindFilmGenreByIdThereIdIsNotExist() {
        assertThatThrownBy(() -> filmGenreStorage.findFilmGenreById(10L))
                .isInstanceOf(FilmGenreNotFoundException.class)
                .hasMessageContaining("Жанр фильма не найден. ID: 10");
    }

    @Test
    public void testFindAllShouldBeListOf6Items() {
        List<FilmGenre> filmsGenres = new ArrayList<>(filmGenreStorage.findAll());
        assertThat(filmsGenres.size()).isEqualTo(6);
        List<FilmGenreEnum> filmGenreEnums = new ArrayList<>(List.of(FilmGenreEnum.values()));
        for (int i = 0; i < 6; i++) {
            assertThat(filmsGenres.get(i).getId()).isEqualTo(i + 1);
            assertThat(filmsGenres.get(i).getName()).isEqualTo(filmGenreEnums.get(i));
        }
    }

}