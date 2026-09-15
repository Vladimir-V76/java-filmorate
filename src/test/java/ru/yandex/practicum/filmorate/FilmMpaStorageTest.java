package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.film.FilmGenreDto;
import ru.yandex.practicum.filmorate.dto.film.FilmMpaDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.FilmMpa;
import ru.yandex.practicum.filmorate.model.film.FilmMpaEnum;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.mpa.FilmMpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class FilmMpaStorageTest {

    @Autowired
    private FilmMpaDbStorage filmMpaStorage;
    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;

    @BeforeEach
    public void createTestData() {
        NewUserRequest newUser1 = new NewUserRequest();
        newUser1.setEmail("user1@bk.ru");
        newUser1.setLogin("user1login");
        newUser1.setName("name 1");
        newUser1.setBirthday(LocalDate.parse("1991-01-01"));
        newUser1.setFriendsSet(Set.of());
        newUser1.setConfirmFriends(List.of(setConfirmFriend(2L, false)));

        NewUserRequest newUser2 = new NewUserRequest();
        newUser2.setEmail("user2@bk.ru");
        newUser2.setLogin("user2login");
        newUser2.setName("name 2");
        newUser2.setBirthday(LocalDate.parse("1992-02-02"));
        newUser2.setFriendsSet(Set.of(3L));
        newUser2.setConfirmFriends(List.of(
                setConfirmFriend(3L, true),
                setConfirmFriend(1L, false)
        ));

        NewUserRequest newUser3 = new NewUserRequest();
        newUser3.setEmail("user3@bk.ru");
        newUser3.setLogin("user3login");
        newUser3.setName("name 3");
        newUser3.setBirthday(LocalDate.parse("1993-03-03"));
        newUser3.setFriendsSet(Set.of(2L));
        newUser3.setConfirmFriends(List.of(setConfirmFriend(2L, true)));

        NewFilmRequest newFilm1 = new NewFilmRequest();
        newFilm1.setName("Film 1");
        newFilm1.setDescription("Description film 1");
        newFilm1.setReleaseDate(LocalDate.parse("2001-01-01"));
        newFilm1.setDuration(100);
        newFilm1.setGenres(List.of(
                new FilmGenreDto(1L, "COMEDY"),
                new FilmGenreDto(4L, "THRILLER"),
                new FilmGenreDto(3L, "CARTOON")
        ));
        newFilm1.setMpa(new FilmMpaDto(1L, "G"));
        Film film1 = (FilmMapper.mapToFilmFromNewFilmRequest(newFilm1));
        film1.setLikedFilm(Set.of(1L, 2L, 3L));

        NewFilmRequest newFilm2 = new NewFilmRequest();
        newFilm2.setName("Film 2");
        newFilm2.setDescription("Description film 2");
        newFilm2.setReleaseDate(LocalDate.parse("2001-02-01"));
        newFilm2.setDuration(102);
        newFilm2.setGenres(List.of(
                new FilmGenreDto(2L, "DRAMA"),
                new FilmGenreDto(5L, "DOCUMENTARY")
        ));
        newFilm2.setMpa(new FilmMpaDto(2L, "PG"));
        Film film2 = (FilmMapper.mapToFilmFromNewFilmRequest(newFilm2));
        film2.setLikedFilm(Set.of(2L, 3L));

        NewFilmRequest newFilm3 = new NewFilmRequest();
        newFilm3.setName("Film 3");
        newFilm3.setDescription("Description film 3");
        newFilm3.setReleaseDate(LocalDate.parse("2001-03-01"));
        newFilm3.setDuration(103);
        newFilm3.setGenres(List.of(new FilmGenreDto(6L, "ACTION")));
        newFilm3.setMpa(new FilmMpaDto(3L, "PG_13"));
        Film film3 = (FilmMapper.mapToFilmFromNewFilmRequest(newFilm3));
        film3.setLikedFilm(Set.of(1L));

        userStorage.create(UserMapper.mapToUserFormNewUserRequest(newUser1));
        userStorage.create(UserMapper.mapToUserFormNewUserRequest(newUser2));
        userStorage.create(UserMapper.mapToUserFormNewUserRequest(newUser3));

        filmStorage.create(film1);
        filmStorage.update(film1);
        filmStorage.create(film2);
        filmStorage.update(film2);
        filmStorage.create(film3);
        filmStorage.update(film3);
    }

    ConfirmFriend setConfirmFriend(Long friendId, boolean isConfirmed) {
        ConfirmFriend confirmFriend = new ConfirmFriend();
        confirmFriend.setFriendId(friendId);
        confirmFriend.setConfirmation(isConfirmed);
        return confirmFriend;
    }

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

    @Test
    void filmMpaOnId1MastBeDeletedInDbAndTablesFilms() {
        FilmMpa filmMpa = new FilmMpa();
        filmMpa.setId(1L);
        filmMpaStorage.delete(filmMpa);
        List<FilmMpa> filmMpaS = new ArrayList<>(filmMpaStorage.findAll());
        assertThat(filmMpaS.size()).isEqualTo(4);

        assertThatThrownBy(() -> filmMpaStorage.findFilmMpaById(1L))
                .isInstanceOf(MpaNotFoundException.class)
                .hasMessageContaining("Рейтинг фильма не найден. ID: 1");

        List<Film> films = new ArrayList<>(filmStorage.findAll());
        assertThat(films.size()).isEqualTo(3);

        films.forEach(f -> {
            if (f.getMpa() != null) {
                assertThat(f.getMpa().getId()).isNotEqualTo(1);
            }
        });
    }
}