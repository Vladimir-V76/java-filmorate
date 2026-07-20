package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateItemException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUserById(Long id) {
        validationId(id);
        return findUserById(id);
    }

    public User addOnFriends(Long id, Long friendId) {
        validationId(id);
        validationId(friendId);
        User user = findUserById(id);
        User friend = findUserById(friendId);
        if (!user.getFriendsSet().add(friend.getId())) {
            throw new DuplicateItemException("Пользователь с id=" + friendId +
                    " уже находится с списке друзей пользователя с id=" + id);
        }
        if (!friend.getFriendsSet().add(user.getId())) {
            throw new DuplicateItemException("Пользователь с id=" + friendId +
                    " уже находится с списке друзей пользователя с id=" + id);
        }
        log.trace("Пользователь с id={} успешно добавлен в друзья пользователю с id={} и наоборот", friendId, id);
        return friend;
    }

    public User deleteOnFriends(Long id, Long friendId) {
        validationId(id);
        validationId(friendId);
        User user = findUserById(id);
        User friend = findUserById(friendId);
        user.getFriendsSet().remove(friend.getId());
        friend.getFriendsSet().remove(user.getId());
        log.trace("Пользователь с id={} успешно удален из друзей пользователя с id={} и наоборот", friendId, id);
        return friend;
    }

    public List<User> getFriendsListUserById(Long id) {
        validationId(id);
        User user = findUserById(id);

        return user.getFriendsSet().stream()
                .map(this::findUserById)
                .toList();
    }

    public List<User> getListMutualFriends(Long id, Long otherId) {
        validationId(id);
        validationId(otherId);
        User user = findUserById(id);
        User otherUser = findUserById(otherId);
        return user.getFriendsSet().stream()
                .filter(u -> otherUser.getFriendsSet().contains(u))
                .map(this::findUserById)
                .toList();
    }

    public void validationId(Long id) {
        if (id <= 0) {
            throw new ValidationUserException("Неверный id=" + id + ". Должно быть положительное число.");
        }
    }

    public User findUserById(Long id) {
        Optional<User> searchUser = userStorage.findAll().stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .findFirst();
        if (searchUser.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }
        log.trace("Пользователь с id={} успешно найден", id);
        return searchUser.get();
    }
}
