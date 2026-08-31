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
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.film.*;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest(classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class FilmStorageTests {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;
    private final NewFilmRequest newFilm = new NewFilmRequest();

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
        updateFilm.setId(1L);
        updateFilm.setName("updateFilmName");
        updateFilm.setDescription("updateFilmDescription");
        updateFilm.setReleaseDate(LocalDate.parse("2002-02-02"));
        updateFilm.setDuration(200);
        updateFilm.setGenres(List.of(new FilmGenreDto(3L, "CARTOON")));
        updateFilm.setMpa(new FilmMpaDto(2L, "PG"));

        filmStorage.update(FilmMapper.mapToFilmFromUpdateFilmRequest(updateFilm));

        Film film = filmStorage.findFilmById(1L);
        assertThat(film.getId()).isEqualTo(1L);
        assertThat(film.getName()).isEqualTo("updateFilmName");
        assertThat(film.getDescription()).isEqualTo("updateFilmDescription");
        assertThat(film.getReleaseDate()).isEqualTo(LocalDate.parse("2002-02-02"));
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

    @Test
    void testFindListFilmGenresByFilmIdShouldBy3ItemForFilm1() {
        List<FilmGenre> filmGenres = filmStorage.findListFilmGenresByFilmId(1L);
        assertThat(filmGenres.size()).isEqualTo(3);
        assertThat(filmGenres)
                .isEqualTo(List.of(
                        setFilmGenre(1L, FilmGenreEnum.COMEDY),
                        setFilmGenre(3L, FilmGenreEnum.CARTOON),
                        setFilmGenre(4L, FilmGenreEnum.THRILLER)
                ));
    }

    @Test
    void testFindSetFilmLikedUserByFilmIdShouldBy3ItemForFilm1() {
        Set<Long> filmLikedUser = filmStorage.findSetFilmLikedUserByFilmId(1L);
        assertThat(filmLikedUser.size()).isEqualTo(3);
        assertThat(filmLikedUser).isEqualTo(Set.of(1L, 2L, 3L));
    }

    @Test
    void testFindSetFilmLikedUserBUserIdShouldBy2ItemForFilm1() {
        Set<Long> filmLikedUser = filmStorage.findSetFilmLikedUserByUserId(1L);
        assertThat(filmLikedUser.size()).isEqualTo(2);
        assertThat(filmLikedUser).isEqualTo(Set.of(1L, 3L));
    }

    @Test
    void filmOnId1MastBeDeletedInDbAndTablesFilmGenresAndLikedFilmUser() {
        Film film = new Film();
        film.setId(1L);
        filmStorage.delete(film);
        List<Film> films = new ArrayList<>(filmStorage.findAll());
        assertThat(films.size()).isEqualTo(2);

        assertThatThrownBy(() -> filmStorage.findFilmById(1L))
                .isInstanceOf(FilmNotFoundException.class)
                .hasMessageContaining("Фильм не найден. ID: 1");

        List<FilmGenre> filmGenres = filmStorage.findListFilmGenresByFilmId(1L);
        assertThat(filmGenres.size()).isEqualTo(0);

        Set<Long> filmLikedUsers = filmStorage.findSetFilmLikedUserByFilmId(1L);
        assertThat(filmLikedUsers.size()).isEqualTo(0);
    }

}