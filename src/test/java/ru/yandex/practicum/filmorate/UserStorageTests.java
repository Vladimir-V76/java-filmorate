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
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserStorageTests {

    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private FilmDbStorage filmStorage;
    private final NewUserRequest newUser = new NewUserRequest();

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

    void fillNewUserRequest() {
        newUser.setEmail("newEmail@email.ru");
        newUser.setLogin("loginNewUser");
        newUser.setBirthday(LocalDate.parse("2000-01-01"));
        newUser.setFriendsSet(Set.of(1L));
        newUser.setConfirmFriends(List.of(
                setConfirmFriend(1L, true), setConfirmFriend(2L, false))
        );
    }

    @Test
    void shouldBe1AfterFindUserByIdThereIdIs1() {
        User user = userStorage.findUserById(1L);
        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    void shouldUserNotFoundExceptionFromFindUserByIdThereIdIsNotExist() {
        assertThatThrownBy(() -> userStorage.findUserById(10L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь не найден. ID: 10");

    }

    @Test
    void testCreateMustMeetRequiredRequirementsOnCorrectFields() {
        //поле name должно принять значение поля login если оно пустое
        fillNewUserRequest();
        userStorage.create(UserMapper.mapToUserFormNewUserRequest(newUser));

        User user = userStorage.findUserById(4L);
        assertThat(user.getName()).isEqualTo("loginNewUser");

        //должен вернуться id=5 при создании нового user и поля отправленного user соответствовать полям созданного
        newUser.setName("nameNewUser");
        userStorage.create(UserMapper.mapToUserFormNewUserRequest(newUser));

        user = userStorage.findUserById(5L);
        assertThat(user.getId()).isEqualTo(5L);
        assertThat(user.getEmail()).isEqualTo("newEmail@email.ru");
        assertThat(user.getLogin()).isEqualTo("loginNewUser");
        assertThat(user.getName()).isEqualTo("nameNewUser");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.parse("2000-01-01"));
        assertThat(user.getFriendsSet()).isEqualTo((Set.of(1L)));
        assertThat(user.getConfirmFriends())
                .isEqualTo(Map.of(
                        1L, setConfirmFriend(1L, true),
                        2L, setConfirmFriend(2L, false)
                ));
    }

    @Test
    public void testUpdateMeetRequiredRequirementsOnCorrectFields() {

        fillNewUserRequest();
        userStorage.create(UserMapper.mapToUserFormNewUserRequest(newUser));

        //поле name должно принять значение поля login если оно пустое
        //должны поля отправленного updateUser соответствовать полям полученного user
        UpdateUserRequest updateUser = new UpdateUserRequest();
        updateUser.setId(4L);
        updateUser.setEmail("updateEmail@email.ru");
        updateUser.setLogin("loginUpdateUser");
        updateUser.setBirthday(LocalDate.parse("2001-02-02"));
        updateUser.setConfirmFriends(List.of(setConfirmFriend(2L, false)));

        userStorage.update(UserMapper.mapToUserFromUpdateUserRequest(updateUser));

        User user = userStorage.findUserById(4L);
        assertThat(user.getEmail()).isEqualTo("updateEmail@email.ru");
        assertThat(user.getLogin()).isEqualTo("loginUpdateUser");
        assertThat(user.getName()).isEqualTo("loginUpdateUser");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.parse("2001-02-02"));
        assertThat(user.getFriendsSet()).isEqualTo((Set.of()));
        assertThat(user.getConfirmFriends()).isEqualTo(Map.of(2L, setConfirmFriend(2L, false)));

        //должно обновиться поле name
        updateUser.setName("nameUpdateUser");
        userStorage.update(UserMapper.mapToUserFromUpdateUserRequest(updateUser));
        user = userStorage.findUserById(4L);
        assertThat(user.getName()).isEqualTo("nameUpdateUser");
    }

    @Test
    public void testFindAllShouldBeListOf3Items() {
        List<User> users = new ArrayList<>(userStorage.findAll());
        assertThat(users.size()).isEqualTo(3);

        assertThat(users.getFirst().getId()).isEqualTo(1);
        assertThat(users.getFirst().getEmail()).isEqualTo("user1@bk.ru");
        assertThat(users.getFirst().getLogin()).isEqualTo("user1login");
        assertThat(users.getFirst().getName()).isEqualTo("name 1");
        assertThat(users.getFirst().getBirthday()).isEqualTo(LocalDate.parse("1991-01-01"));
        assertThat(users.getFirst().getFriendsSet()).isEqualTo((Set.of()));
        assertThat(users.getFirst().getConfirmFriends())
                .isEqualTo(Map.of(2L, setConfirmFriend(2L, false)));

        assertThat(users.get(1).getId()).isEqualTo(2);
        assertThat(users.get(1).getEmail()).isEqualTo("user2@bk.ru");
        assertThat(users.get(1).getLogin()).isEqualTo("user2login");
        assertThat(users.get(1).getName()).isEqualTo("name 2");
        assertThat(users.get(1).getBirthday()).isEqualTo(LocalDate.parse("1992-02-02"));
        assertThat(users.get(1).getFriendsSet()).isEqualTo((Set.of(3L)));
        assertThat(users.get(1).getConfirmFriends())
                .isEqualTo(Map.of(
                        3L, setConfirmFriend(3L, true),
                        1L, setConfirmFriend(1L, false)
                ));

        assertThat(users.get(2).getId()).isEqualTo(3);
        assertThat(users.get(2).getEmail()).isEqualTo("user3@bk.ru");
        assertThat(users.get(2).getLogin()).isEqualTo("user3login");
        assertThat(users.get(2).getName()).isEqualTo("name 3");
        assertThat(users.get(2).getBirthday()).isEqualTo(LocalDate.parse("1993-03-03"));
        assertThat(users.get(2).getFriendsSet()).isEqualTo((Set.of(2L)));
        assertThat(users.get(2).getConfirmFriends())
                .isEqualTo(Map.of(2L, setConfirmFriend(2L, true)));
    }

    @Test
    void testFindListConfirmedFriendsByUserIdShouldBy1ItemForUser1() {
        Map<Long, ConfirmFriend> confirmedFriends = userStorage.findListConfirmedFriendsByUserId(1L);
        assertThat(confirmedFriends.size()).isEqualTo(1);
        assertThat(confirmedFriends).isEqualTo(Map.of(2L, setConfirmFriend(2L, false)));
    }

    @Test
    void userOnId1MastBeDeletedInDbAndTablesFriendsAndLikedFilmUser() {
        User user = new User();
        user.setId(1L);
        userStorage.delete(user);
        List<User> users = new ArrayList<>(userStorage.findAll());
        assertThat(users.size()).isEqualTo(2);

        assertThatThrownBy(() -> userStorage.findUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь не найден. ID: 1");

        Map<Long, ConfirmFriend> userConfirmFriends = userStorage.findListConfirmedFriendsByUserId(1L);
        assertThat(userConfirmFriends.size()).isEqualTo(0);

        Set<Long> filmLikedUsers = filmStorage.findSetFilmLikedUserByUserId(1L);
        assertThat(filmLikedUsers.size()).isEqualTo(0);
    }
}