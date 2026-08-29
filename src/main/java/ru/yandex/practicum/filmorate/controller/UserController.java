package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserRequest user) {
        return userService.create(user);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest user) {
        return userService.update(user);
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping({"/", "//friends"})
    public UserDto noneId() {
        log.trace("Передан Get запрос получение пользователя по id без id");
        throw new ValidationUserException("Id должен быть указан.");
    }

    @GetMapping("/{id}")
    @ResponseBody
    public UserDto getUserById(
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id) {
        log.trace("Передан Get запрос на получение пользователя с id={}", id);
        return userService.getUserById(id);
    }

    @PutMapping({"//friends/{friendId}", "/{id}/friends/", "//friends/"})
    public UserDto noneIdOFFriendIdInPutMapping() {
        log.trace("Передан Put запрос на добавление пользователя в друзья без id");
        throw new ValidationUserException("Id пользователя и id друга должны быть указаны.");
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public UserDto addOnFriends(
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id,

            @Min(value = 1, message = "FriendId должно быть числом положительным")
            @PathVariable Long friendId) {
        log.trace("Передан Put запрос на добавление пользователя с id={} в друзья пользователю с id={}", friendId, id);
        return userService.addOnFriends(id, friendId);
    }

    @DeleteMapping({"//friends/{friendId}", "/{id}/friends/", "//friends/"})
    public UserDto noneIdOFFriendIdInDeleteMapping() {
        log.trace("Передан Delete запрос на удаление из друзей пользователя без id");
        throw new ValidationUserException("Id пользователя и id друга должны быть указаны.");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public UserDto deleteOnFriends(
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id,

            @Min(value = 1, message = "FriendId должно быть числом положительным")
            @PathVariable Long friendId) {
        log.trace("Передан Delete запрос на удаление из друзей пользователя с id={} пользователя с id={}", id, friendId);
        return userService.deleteOnFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseBody
    public List<UserDto> getFriendsListUserById(
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id) {
        log.trace("Передан Get запрос на получение списка друзей пользователя с id={}", id);
        return userService.getFriendsListUserById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseBody
    public List<UserDto> getListMutualFriends(
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id,

            @Min(value = 1, message = "OtherId должно быть числом положительным")
            @PathVariable Long otherId) {
        log.trace("Передан Get запрос на получения списка общих друзей пользователя с id={} и пользователя с id={}",
                id, otherId);
        return userService.getListMutualFriends(id, otherId);
    }
}
