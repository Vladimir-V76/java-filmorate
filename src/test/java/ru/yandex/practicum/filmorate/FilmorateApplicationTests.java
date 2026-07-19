package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {

    @BeforeEach
    void clearFilmsAndUsers() {
        FilmController.clear();
        UserController.clear();
    }

    @Test
    void contextLoads() {
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturn415IfPostRequestOnFilmIsEmpty() {
        String emptyRequest = "{}";
        ResponseEntity<String> response = restTemplate.postForEntity("/films", emptyRequest, String.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    }

    @Test
    void shouldReturn400IfPutRequestOnFilmIsEmpty() {
        String emptyRequest = "{}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(emptyRequest, headers);
        ResponseEntity<String> responseFromPut = restTemplate.exchange(
                "/films", HttpMethod.PUT, requestEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseFromPut.getStatusCode());
    }

    @Test
    void shouldReturn200IfPostRequestOnFilmIsCorrect() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("1234567890");
        film.setReleaseDate(LocalDate.of(1987, 12, 12));
        film.setDuration(140);
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturn200AndFieldsMustBeChangedIfPutRequestOnFilmIsCorrect() {
        Film film1 = new Film();
        film1.setName("name1");
        film1.setDescription("description1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(100);

        restTemplate.postForEntity("/films", film1, Film.class);

        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("name2");
        film2.setDescription("description1");
        film2.setReleaseDate(LocalDate.of(2001, 2, 2));
        film2.setDuration(101);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Film> requestEntity = new HttpEntity<>(film2, headers);
        ResponseEntity<Film> responseFromPut = restTemplate.exchange(
                "/films", HttpMethod.PUT, requestEntity, Film.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatusCode());
        Film responseFilm = responseFromPut.getBody();
        Assertions.assertNotNull(responseFilm);
        assertEquals(film2.getName(), responseFilm.getName());
        assertEquals(film2.getDescription(), responseFilm.getDescription());
        assertEquals(film2.getReleaseDate(), responseFilm.getReleaseDate());
        assertEquals(film2.getDuration(), responseFilm.getDuration());
    }

    @Test
    void shouldBeNameNotBeEmptyIfFieldNameFilmDTOIsNullOrBlank() {
        Film film = new Film();
        film.setName(null);
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

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("1234567890");
        film1.setReleaseDate(LocalDate.of(1987, 12, 12));
        film1.setDuration(140);

        FilmController.create(film1);

        film.setId(1L);
        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.update(film))
                .withMessageContaining("Название не должно быть пустым");

        film.setName(null);
        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.update(film))
                .withMessageContaining("Название не должно быть пустым");
    }

    @Test
    void shouldBeFilmWithId2NotFoundIfIdFilmForUpdateDoesNotExist() {
        Film film = new Film();
        film.setName("film");
        film.setDescription("1234567890");
        film.setReleaseDate(LocalDate.of(1987, 12, 12));
        film.setDuration(140);
        FilmController.create(film);

        Film film1 = new Film();
        film1.setId(2L);
        film1.setName("film1");
        film1.setDescription("1234567890");
        film1.setReleaseDate(LocalDate.of(1987, 12, 12));
        film1.setDuration(140);

        assertThatExceptionOfType(FilmNotFoundException.class)
                .isThrownBy(() -> FilmController.update(film1))
                .withMessageContaining("Фильм с id=2 не найден");
    }

    @Test
    void shouldBeMaximumDescriptionLengthIs200CharactersIfFieldDescriptionFilmDTOLength201Characters() {
        Film film = new Film();
        film.setName("name");
        film.setReleaseDate(LocalDate.of(1987, 12, 12));
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < 103; i++) {
            description.append(i);
        }
        description.append("11");
        film.setDescription(String.valueOf(description));
        film.setDuration(140);

        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Максимальная длина описания — 200 символов");

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("1234567890");
        film1.setReleaseDate(LocalDate.of(1987, 12, 12));
        film1.setDuration(140);

        FilmController.create(film1);

        film.setId(1L);
        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.update(film))
                .withMessageContaining("Максимальная длина описания — 200 символов");
    }

    @Test
    void shouldBeRequestIsIncompleteSomeInformationIsMissingIfFieldDescriptionFilmDTOIsEmpty() {
        Film film = new Film();
        film.setName("name");
        film.setDescription(null);
        film.setReleaseDate(LocalDate.of(1987, 12, 12));
        film.setDuration(140);

        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Запрос не полный, отсутствует часть информации");

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("1234567890");
        film1.setReleaseDate(LocalDate.of(1987, 12, 12));
        film1.setDuration(140);

        FilmController.create(film1);

        film.setId(1L);
        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.update(film))
                .withMessageContaining("Запрос не полный, отсутствует часть информации");
    }

    @Test
    void shouldReturn200AndFieldChangedIfFieldDescriptionFilmDTOLength200Characters() {
        Film film = new Film();
        film.setName("name");
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < 103; i++) {
            description.append(i);
        }
        description.append("1");
        film.setDescription(String.valueOf(description));
        film.setReleaseDate(LocalDate.of(1987, 12, 12));
        film.setDuration(140);
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("1234567890");
        film1.setReleaseDate(LocalDate.of(1987, 12, 12));
        film1.setDuration(140);

        FilmController.create(film1);

        film.setId(2L);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Film> requestEntity = new HttpEntity<>(film, headers);
        ResponseEntity<Film> responseFromPut = restTemplate.exchange(
                "/films", HttpMethod.PUT, requestEntity, Film.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatusCode());
        Film responseFilm = responseFromPut.getBody();
        Assertions.assertNotNull(responseFilm);
        assertEquals(film.getDescription(), responseFilm.getDescription());
    }

    @Test
    void shouldBeReleaseDateIsNoEarlierThanDecember28_1895IfFieldReleaseDateFilmDTODecember27_1895() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("1234567890");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(140);

        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Дата релиза — не раньше 28 декабря 1895 года");

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("1234567890");
        film1.setReleaseDate(LocalDate.of(1987, 12, 12));
        film1.setDuration(140);

        FilmController.create(film1);

        film.setId(1L);
        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.update(film))
                .withMessageContaining("Дата релиза — не раньше 28 декабря 1895 года");
    }

    @Test
    void shouldBeRequestIsIncompleteSomeInformationIsMissingIfFieldReleaseDateFilmDTOIsEmpty() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("1234567890");
        film.setReleaseDate(null);
        film.setDuration(140);

        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Запрос не полный, отсутствует часть информации");

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("1234567890");
        film1.setReleaseDate(LocalDate.of(1987, 12, 12));
        film1.setDuration(140);

        FilmController.create(film1);

        film.setId(1L);
        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.update(film))
                .withMessageContaining("Запрос не полный, отсутствует часть информации");
    }

    @Test
    void shouldReturn200AndFieldChangedIfFieldReleaseDateFilmDTODecember28_1895() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("1234567890");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(140);
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("1234567890");
        film1.setReleaseDate(LocalDate.of(1987, 12, 12));
        film1.setDuration(140);

        FilmController.create(film1);

        film.setId(2L);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Film> requestEntity = new HttpEntity<>(film, headers);
        ResponseEntity<Film> responseFromPut = restTemplate.exchange(
                "/films", HttpMethod.PUT, requestEntity, Film.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatusCode());
        Film responseFilm = responseFromPut.getBody();
        Assertions.assertNotNull(responseFilm);
        assertEquals(film.getReleaseDate(), responseFilm.getReleaseDate());
    }

    @Test
    void shouldBeLengthOfMovieMustBePositiveIfFieldDurationFilmDTOIs0OrNegative() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("1234567890");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(0);

        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Продолжительность фильма должна быть положительным числом");

        film.setDuration(-1);
        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.create(film))
                .withMessageContaining("Продолжительность фильма должна быть положительным числом");

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("1234567890");
        film1.setReleaseDate(LocalDate.of(1987, 12, 12));
        film1.setDuration(140);

        FilmController.create(film1);

        film.setId(1L);
        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.update(film))
                .withMessageContaining("Продолжительность фильма должна быть положительным числом");

        film.setDuration(0);
        assertThatExceptionOfType(ValidationFilmException.class)
                .isThrownBy(() -> FilmController.update(film))
                .withMessageContaining("Продолжительность фильма должна быть положительным числом");
    }

    @Test
    void shouldReturn415IfPostRequestOnUserIsEmpty() {
        String emptyRequest = "{}";
        ResponseEntity<String> response = restTemplate.postForEntity("/users", emptyRequest, String.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    }

    @Test
    void shouldReturn400IfPutRequestOnUserIsEmpty() {
        String emptyRequest = "{}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(emptyRequest, headers);
        ResponseEntity<String> responseFromPut = restTemplate.exchange(
                "/users", HttpMethod.PUT, requestEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseFromPut.getStatusCode());
    }

    @Test
    void shouldReturn200IfPostRequestOnUserIsCorrect() {
        User user = new User();
        user.setEmail("notnull@yandex.ru");
        user.setLogin("user");
        user.setName("1234567890");
        user.setBirthday(LocalDate.of(1987, 12, 12));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturn200AndFieldsMustBeChangedIfPutRequestOnUserIsCorrect() {
        User user1 = new User();
        user1.setEmail("notnull1@yandex.ru");
        user1.setLogin("user1");
        user1.setName("11111");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        restTemplate.postForEntity("/users", user1, User.class);

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("notnull2@yandex.ru");
        user2.setLogin("user2");
        user2.setName("22222");
        user2.setBirthday(LocalDate.of(2001, 2, 2));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(user2, headers);
        ResponseEntity<User> responseFromPut = restTemplate.exchange(
                "/users", HttpMethod.PUT, requestEntity, User.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatusCode());
        User responseUser = responseFromPut.getBody();
        Assertions.assertNotNull(responseUser);
        assertEquals(user2.getEmail(), responseUser.getEmail());
        assertEquals(user2.getLogin(), responseUser.getLogin());
        assertEquals(user2.getName(), responseUser.getName());
        assertEquals(user2.getBirthday(), responseUser.getBirthday());
    }

    @Test
    void shouldBeUserWithId2NotFoundIfIdUserForUpdateDoesNotExist() {
        User user = new User();
        user.setEmail("notnull@yandex.ru");
        user.setLogin("user");
        user.setName("00000");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        UserController.create(user);

        User user1 = new User();
        user1.setId(2L);
        user1.setEmail("notnull1@yandex.ru");
        user1.setLogin("user1");
        user1.setName("11111");
        user1.setBirthday(LocalDate.of(2001, 2, 2));

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> UserController.update(user1))
                .withMessageContaining("Пользователь с id=2 не найден");
    }

    @Test
    void shouldBeEmailCannotBeEmptyAndMustContainSymbolIfFieldEmailUserDTOIsNullEmptyOrNotContainSymbol() {
        User user = new User();
        user.setEmail(null);
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

        User user1 = new User();
        user1.setEmail("notnull@yandex.ru");
        user1.setLogin("user");
        user1.setName("00000");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        UserController.create(user1);

        user.setId(1L);
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.update(user))
                .withMessageContaining("Электронная почта не может быть пустой и должна содержать символ @");

        user.setEmail(" ");
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.update(user))
                .withMessageContaining("Электронная почта не может быть пустой и должна содержать символ @");

        user.setEmail(null);
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.update(user))
                .withMessageContaining("Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void shouldReturn200AndFieldChangedIfFieldEmailUserDTOIsCorrect() {
        User user = new User();
        user.setEmail("1234@mail.ru");
        user.setLogin("user");
        user.setName("1234567890");
        user.setBirthday(LocalDate.of(1987, 12, 12));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User user1 = new User();
        user1.setEmail("notnull@yandex.ru");
        user1.setLogin("user");
        user1.setName("00000");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        user1.setId(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(user1, headers);
        ResponseEntity<User> responseFromPut = restTemplate.exchange(
                "/users", HttpMethod.PUT, requestEntity, User.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatusCode());
        User responseUser = responseFromPut.getBody();
        Assertions.assertNotNull(responseUser);
        assertEquals(user1.getEmail(), responseUser.getEmail());
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

        user.setLogin(" ");
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.create(user))
                .withMessageContaining("Логин не может быть пустым и содержать пробелы");

        User user1 = new User();
        user1.setEmail("notnull@yandex.ru");
        user1.setLogin("user");
        user1.setName("00000");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        restTemplate.postForEntity("/users", user1, User.class);

        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.update(user))
                .withMessageContaining("Логин не может быть пустым и содержать пробелы");

        user.setLogin(" user ");
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.update(user))
                .withMessageContaining("Логин не может быть пустым и содержать пробелы");

        user.setLogin(null);
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.update(user))
                .withMessageContaining("Логин не может быть пустым и содержать пробелы");
    }

    @Test
    void shouldReturn200AndFieldChangedIfFieldLoginUserDTOIsCorrect() {
        User user = new User();
        user.setEmail("1234@mail.ru");
        user.setLogin("user");
        user.setName("1234567890");
        user.setBirthday(LocalDate.of(1987, 12, 12));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User user1 = new User();
        user1.setEmail("notnull@yandex.ru");
        user1.setLogin("user1");
        user1.setName("00000");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        user1.setId(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(user1, headers);
        ResponseEntity<User> responseFromPut = restTemplate.exchange(
                "/users", HttpMethod.PUT, requestEntity, User.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatusCode());
        User responseUser = responseFromPut.getBody();
        Assertions.assertNotNull(responseUser);
        assertEquals(user1.getLogin(), responseUser.getLogin());
    }

    @Test
    void shouldReturn200AndFieldNameChangedOnFieldLoginIfFieldNameUserDTOIsEmpty() {
        User user = new User();
        user.setEmail("1234@mail.ru");
        user.setLogin("user");
        user.setName(null);
        user.setBirthday(LocalDate.of(1987, 12, 12));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User responseUser = response.getBody();
        Assertions.assertNotNull(responseUser);
        assertEquals(user.getLogin(), responseUser.getName());

        user.setLogin("user");
        user.setName(" ");

        response = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseUser = response.getBody();
        Assertions.assertNotNull(responseUser);
        assertEquals(user.getLogin(), responseUser.getName());

        User user1 = new User();
        user1.setEmail("notnull@yandex.ru");
        user1.setLogin("user1");
        user1.setName(null);
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        user1.setId(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(user1, headers);
        ResponseEntity<User> responseFromPut = restTemplate.exchange(
                "/users", HttpMethod.PUT, requestEntity, User.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatusCode());
        responseUser = responseFromPut.getBody();
        Assertions.assertNotNull(responseUser);
        assertEquals(user1.getLogin(), responseUser.getName());

        user1.setLogin("user2");
        user1.setName(" ");

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(user1, headers);
        responseFromPut = restTemplate.exchange("/users", HttpMethod.PUT, requestEntity, User.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatusCode());
        responseUser = responseFromPut.getBody();
        Assertions.assertNotNull(responseUser);
        assertEquals(user1.getLogin(), responseUser.getName());
    }

    @Test
    void shouldReturn200AndFieldChangedIfFieldNameUserDTOIsCorrect() {
        User user = new User();
        user.setEmail("1234@mail.ru");
        user.setLogin("user");
        user.setName("1234567890");
        user.setBirthday(LocalDate.of(1987, 12, 12));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User user1 = new User();
        user1.setEmail("notnull@yandex.ru");
        user1.setLogin("user1");
        user1.setName("00000");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        user1.setId(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(user1, headers);
        ResponseEntity<User> responseFromPut = restTemplate.exchange(
                "/users", HttpMethod.PUT, requestEntity, User.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatusCode());
        User responseUser = responseFromPut.getBody();
        Assertions.assertNotNull(responseUser);
        assertEquals(user1.getName(), responseUser.getName());
    }

    @Test
    void shouldBeBirthdayCannotBeInTheFutureIfFieldBirthdayUserDTOIsContainDateInTheFuture() {
        User user = new User();
        user.setEmail("1234@1223.ru");
        user.setLogin("user");
        user.setName("1234567890");
        user.setBirthday(LocalDate.now().plusDays(1L));

        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.create(user))
                .withMessageContaining("Дата рождения не может быть в будущем");

        User user1 = new User();
        user1.setEmail("notnull@yandex.ru");
        user1.setLogin("user1");
        user1.setName("00000");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        UserController.create(user1);

        user.setId(1L);
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.update(user))
                .withMessageContaining("Дата рождения не может быть в будущем");
    }

    @Test
    void shouldBeRequestIsIncompleteAndBirthdayIsMissingIfFieldBirthdayUserDTOIsEmpty() {
        User user = new User();
        user.setEmail("1234@1223.ru");
        user.setLogin("user");
        user.setName("1234567890");
        user.setBirthday(null);

        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.create(user))
                .withMessageContaining("Запрос не полный, отсутствует дата рождения");

        User user1 = new User();
        user1.setEmail("notnull@yandex.ru");
        user1.setLogin("user1");
        user1.setName("00000");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        UserController.create(user1);

        user.setId(1L);
        assertThatExceptionOfType(ValidationUserException.class)
                .isThrownBy(() -> UserController.update(user))
                .withMessageContaining("Запрос не полный, отсутствует дата рождения");
    }

    @Test
    void shouldReturn200AndFieldChangedIfFieldBirthdayUserDTOIsToday() {
        User user = new User();
        user.setEmail("1234@mail.ru");
        user.setLogin("user");
        user.setName("1234567890");
        user.setBirthday(LocalDate.now());
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User user1 = new User();
        user1.setEmail("notnull@yandex.ru");
        user1.setLogin("user1");
        user1.setName("00000");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        restTemplate.postForEntity("/users", user, User.class);

        user.setId(2L);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);
        ResponseEntity<User> responseFromPut = restTemplate.exchange(
                "/users", HttpMethod.PUT, requestEntity, User.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatusCode());
        User responseUser = responseFromPut.getBody();
        Assertions.assertNotNull(responseUser);
        assertEquals(user.getBirthday(), responseUser.getBirthday());
    }

}