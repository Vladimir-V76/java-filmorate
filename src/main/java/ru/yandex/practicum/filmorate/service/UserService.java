package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicateItemException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    UserStorage userStorage;
    UserDbStorage userDbStorage;

    public UserService(
            @Qualifier("inDbUserStorage")
            UserStorage userStorage,
            UserDbStorage userDbStorage) {
        this.userStorage = userStorage;
        this.userDbStorage = userDbStorage;
    }

    public UserDto create(NewUserRequest newUser) {
        User user = UserMapper.mapToUserFormNewUserRequest(newUser);
        checkUserValidation(user);
       return UserMapper.mapToUserDto(userStorage.create(user));
    }

    public UserDto update(UpdateUserRequest updateUser) {
        User user = UserMapper.mapToUserFromUpdateUserRequest(updateUser);
        checkUserValidation(user);
        return UserMapper.mapToUserDto(userStorage.update(user));
    }

    public Collection<UserDto> findAll() {
        Collection<User> users = userStorage.findAll();
        users.forEach(u -> u.setConfirmFriends(userDbStorage.findListConfirmedFriendsByUserId(u.getId())));
        users.forEach( u -> u.setFriendsSet(getFriendsSetFromMapConfirmFriends(u.getConfirmFriends())));
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        return UserMapper.mapToUserDto(getUserFromStorageById(id));
    }

    public UserDto addOnFriends(Long id, Long friendId) {
        User user = getUserFromStorageById(id);
        User friend = getUserFromStorageById(friendId);
        Map<Long, ConfirmFriend> userConfirmFriendMap = user.getConfirmFriends();

        if (userConfirmFriendMap.containsKey(friendId)) {
            ConfirmFriend confirmFriend = userConfirmFriendMap.get(friendId);
            if (confirmFriend.isConfirmation()) {
                String message = "Пользователь с id=%s уже находится в списке друзей пользователя с id=%d";
                throw new DuplicateItemException(String.format(message, friendId, id));
            } else {
                user.setConfirmFriends(addConfirmFriendInMap(user, friendId, true));
                user.getFriendsSet().add(friendId);

                friend.setConfirmFriends(addConfirmFriendInMap(friend, id, true));
                friend.getFriendsSet().add(id);
                log.trace("Пользователь с id={} успешно добавлен в друзья пользователю с id={} и наоборот", friendId, id);
            }

        } else {
            user.getFriendsSet().add(friendId);
            user.setConfirmFriends(addConfirmFriendInMap(user, friendId, true));
            friend.setConfirmFriends(addConfirmFriendInMap(friend, id, false));
        }
        userStorage.update(user);
        userStorage.update(friend);
        return UserMapper.mapToUserDto(user);
    }

    public static Map<Long, ConfirmFriend> addConfirmFriendInMap(User user, long id, boolean isConfirmed) {
        Map<Long, ConfirmFriend> confirmFriendMap = user.getConfirmFriends();
        ConfirmFriend confirmFriend = new ConfirmFriend();
        confirmFriend.setFriendId(id);
        confirmFriend.setConfirmation(isConfirmed);
        confirmFriendMap.put(id, confirmFriend);

        return confirmFriendMap;
    }

    public UserDto deleteOnFriends(Long id, Long friendId) {
        User user = getUserFromStorageById(id);
        User friend = getUserFromStorageById(friendId);
        user.getFriendsSet().remove(friend.getId());
        user.getConfirmFriends().remove(friend.getId());
        userStorage.update(user);

        log.trace("Пользователь с id={} успешно удален из друзей пользователя с id={} и наоборот", friendId, id);
        return UserMapper.mapToUserDto(friend);
    }

    public List<UserDto> getFriendsListUserById(Long id) {
        User user = getUserFromStorageById(id);
        return user.getFriendsSet().stream()
                .map(this::getUserFromStorageById)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> getListMutualFriends(Long id, Long otherId) {
        User user = getUserFromStorageById(id);
        User otherUser = getUserFromStorageById(otherId);
        return user.getFriendsSet().stream()
                .filter(u -> otherUser.getFriendsSet().contains(u))
                .map(this::getUserFromStorageById)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    private static void checkUserValidation(User user) {
        String userValidation = "Ok";
        if (user.getBirthday() == null) {
            userValidation = "Запрос не полный, отсутствует дата рождения";
        }
        if (user.getLogin().contains(" ")) {
            userValidation = "Логин не может содержать пробелы";
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (!userValidation.equals("Ok")) {
            throw new ValidationUserException(userValidation);
        }
    }

    public User getUserFromStorageById(Long id) {
        User user = userStorage.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден. ID: " + id));
        user.setConfirmFriends(userDbStorage.findListConfirmedFriendsByUserId(user.getId()));
        user.setFriendsSet(getFriendsSetFromMapConfirmFriends(user.getConfirmFriends()));

        return user;
    }

    public Set<Long> getFriendsSetFromMapConfirmFriends(Map<Long, ConfirmFriend> confirmFriends) {
        return confirmFriends.values().stream()
                .filter(ConfirmFriend::isConfirmation)
                .map(ConfirmFriend::getFriendId)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
