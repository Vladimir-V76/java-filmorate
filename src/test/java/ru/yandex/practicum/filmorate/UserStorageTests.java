package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserStorageTests {

    @Autowired
    private UserDbStorage userStorage;
    private final NewUserRequest newUser = new NewUserRequest();

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
        assertThat(user.getBirthday()).isEqualTo("2000-01-01");
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
        assertThat(user.getBirthday()).isEqualTo("2001-02-02");
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
        assertThat(users.getFirst().getBirthday()).isEqualTo("1991-01-01");
        assertThat(users.getFirst().getFriendsSet()).isEqualTo((Set.of()));
        assertThat(users.getFirst().getConfirmFriends())
                .isEqualTo(Map.of(2L, setConfirmFriend(2L, false)));

        assertThat(users.get(1).getId()).isEqualTo(2);
        assertThat(users.get(1).getEmail()).isEqualTo("user2@bk.ru");
        assertThat(users.get(1).getLogin()).isEqualTo("user2login");
        assertThat(users.get(1).getName()).isEqualTo("name 2");
        assertThat(users.get(1).getBirthday()).isEqualTo("1992-02-02");
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
        assertThat(users.get(2).getBirthday()).isEqualTo("1993-03-03");
        assertThat(users.get(2).getFriendsSet()).isEqualTo((Set.of(2L)));
        assertThat(users.get(2).getConfirmFriends())
                .isEqualTo(Map.of(2L, setConfirmFriend(2L, true)));
    }
}