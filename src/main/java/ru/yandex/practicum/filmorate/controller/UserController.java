package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    UserStorage inMemoryUserStorage;
    UserService userService;

    public UserController(UserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return inMemoryUserStorage.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return inMemoryUserStorage.update(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    @GetMapping({"/", "//friends"})
    public User noneId() {
        log.trace("Передан Get запрос получение пользователя по id без id");
        throw new ValidationUserException("Id должен быть указан.");
    }

    @GetMapping("/{id}")
    @ResponseBody
    public User getUserById(@PathVariable Long id) {
        log.trace("Передан Get запрос на получение пользователя с id={}", id);
        return userService.getUserById(id);
    }

    @PutMapping({"//friends/{friendId}", "/{id}/friends/", "//friends/"})
    public User noneIdOFFriendIdInPutMapping() {
        log.trace("Передан Put запрос на добавление пользователя в друзья без id");
        throw new ValidationUserException("Id пользователя и id друга должны быть указаны.");
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public User addOnFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.trace("Передан Put запрос на добавление пользователя с id={} в друзья пользователю с id={}", friendId, id);
        return userService.addOnFriends(id, friendId);
    }

    @DeleteMapping({"//friends/{friendId}", "/{id}/friends/", "//friends/"})
    public User noneIdOFFriendIdInDeleteMapping() {
        log.trace("Передан Delete запрос на удаление из друзей пользователя без id");
        throw new ValidationUserException("Id пользователя и id друга должны быть указаны.");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public User deleteOnFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.trace("Передан Delete запрос на удаление из друзей пользователя с id={} пользователя с id={}", id, friendId);
        return userService.deleteOnFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseBody
    public List<User> getFriendsListUserById(@PathVariable Long id) {
        log.trace("Передан Get запрос на получение списка друзей пользователя с id={}", id);
        return userService.getFriendsListUserById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseBody
    public List<User> getListMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.trace("Передан Get запрос на получения списка общих друзей пользователя с id={} и пользователя с id={}",
                id, otherId);
        return userService.getListMutualFriends(id, otherId);
    }
}
