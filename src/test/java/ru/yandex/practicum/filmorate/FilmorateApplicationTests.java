package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmorateApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void shouldBeNameNotBeEmptyIfFieldNameFilmDTOIsNullOrBlank() {
        Film film = new Film();
        film.setName(null);
        film.setId(1L);
        film.setDuration(140);
        film.setReleaseDate(LocalDate.of(1987, 12, 12));
        film.setDescription("1234567890");

        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Название не должно быть пустым");

        film.setName("  ");
        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Название не должно быть пустым");

    }

    @Test
    void shouldBeMaximumDescriptionLengthIs200CharactersIfFieldDescriptionFilmDTOLength201Characters() {
        Film film = new Film();
        film.setName("null");
        film.setId(1L);
        film.setDuration(140);
        film.setReleaseDate(LocalDate.of(1987, 12, 12));
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < 103; i++) {
            description.append(i);
        }
        description.append("11");
        film.setDescription(String.valueOf(description));

        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Максимальная длина описания — 200 символов");
    }

    @Test
    void shouldBeReleaseDateIsNoEarlierThanDecember28_1895IfFieldReleaseDateFilmDTODecember27_1895() {
        Film film = new Film();
        film.setName("null");
        film.setId(1L);
        film.setDuration(140);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDescription("1234567890");

        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Дата релиза — не раньше 28 декабря 1895 года");
    }

    @Test
    void shouldBeLengthOfMovieMustBePositiveIfFieldDurationFilmDTOIs0() {
        Film film = new Film();
        film.setName("  ");
        film.setId(1L);
        film.setDuration(0);
        film.setReleaseDate(LocalDate.of(1987, 12, 12));
        film.setDescription("1234567890");

        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Продолжительность фильма должна быть положительным числом");
    }

    @Test
    void shouldBeLengthOfMovieMustBePositiveIfFieldDurationFilmDTOIsNegative() {
        Film film = new Film();
        film.setName("  ");
        film.setId(1L);
        film.setDuration(-1);
        film.setReleaseDate(LocalDate.of(1987, 12, 12));
        film.setDescription("1234567890");

        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Продолжительность фильма должна быть положительным числом");
    }

    @Test
    void shouldBeEmailCannotBeEmptyAndMustContainSymbolIfFieldEmailUserDTOIsNullEmptyOrNotContainSymbol() {
        User user = new User();
        user.setId(1L);
        user.setEmail(null); //"1234@1223.ru"
        user.setLogin("user");
        user.setName("1234567890");
        user.setBirthday(LocalDate.of(1987, 12, 12));

        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.create(user))
                .withMessageContaining("Электронная почта не может быть пустой и должна содержать символ @");

        user.setEmail(" ");
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.create(user))
                .withMessageContaining("Электронная почта не может быть пустой и должна содержать символ @");

        user.setEmail("1234_1223.ru");
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.create(user))
                .withMessageContaining("Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void shouldBeLoginCannotBeEmptyOrContainSpacesIfFieldLoginUserDTOIsNullOrContainSpaces() {
        User user = new User();
        user.setId(1L);
        user.setEmail("1234@1223.ru");
        user.setLogin(null);
        user.setName("1234567890");
        user.setBirthday(LocalDate.of(1987, 12, 12));

        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.create(user))
                .withMessageContaining("Логин не может быть пустым и содержать пробелы");

        user.setLogin(" user ");
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.create(user))
                .withMessageContaining("Логин не может быть пустым и содержать пробелы");
    }

    @Test
    void shouldBeInFieldNameValueFieldLoginIfFieldNameUserDTOIsNullOrBlank() {
        User user = new User();
        user.setId(1L);
        user.setEmail("1234@1223.ru");
        user.setLogin("user");
        user.setName(null);
        user.setBirthday(LocalDate.of(1987, 12, 12));

        UserController.create(user);
        assertEquals(user.getName(), user.getLogin());

        user.setLogin("user1");
        user.setName(" ");
        UserController.create(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void shouldBeBirthdayCannotBeInTheFutureIfFieldBirthdayUserDTOIsContainDateInTheFuture() {
        User user = new User();
        user.setId(1L);
        user.setEmail("1234@1223.ru");
        user.setLogin("user");
        user.setName("1234567890");
        user.setBirthday(LocalDate.now().plusDays(1L));

        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.create(user))
                .withMessageContaining("Дата рождения не может быть в будущем");
    }
}