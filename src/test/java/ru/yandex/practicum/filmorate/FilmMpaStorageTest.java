package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.film.FilmMpa;
import ru.yandex.practicum.filmorate.model.film.FilmMpaEnum;
import ru.yandex.practicum.filmorate.storage.film.mpa.FilmMpaDbStorage;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmMpaStorageTest {

    @Autowired
    private FilmMpaDbStorage filmMpaStorage;

    @Test
    void shouldBy1ToFindFilmMpaById() {
        FilmMpa filmMpa = filmMpaStorage.findFilmMpaById(1L);
        assertThat(filmMpa.getId()).isEqualTo(1L);
    }

    @Test
    void shouldMpaNotFoundExceptionFromFindFilmMpaByIdThereIdIsNotExist() {
        assertThatThrownBy(() -> filmMpaStorage.findFilmMpaById(10L))
                .isInstanceOf(MpaNotFoundException.class)
                .hasMessageContaining("Рейтинг фильма не найден. ID: 10");
    }

    @Test
    public void testFindAllShouldBeListOf5Items() {
        List<FilmMpa> filmsMpa = new ArrayList<>(filmMpaStorage.findAll());
        assertThat(filmsMpa.size()).isEqualTo(5);
        List<FilmMpaEnum> filmMpaEnums = new ArrayList<>(List.of(FilmMpaEnum.values()));
        for (int i = 0; i < 5; i++) {
            assertThat(filmsMpa.get(i).getId()).isEqualTo(i + 1);
            assertThat(filmsMpa.get(i).getName()).isEqualTo(filmMpaEnums.get(i));
        }
    }
}