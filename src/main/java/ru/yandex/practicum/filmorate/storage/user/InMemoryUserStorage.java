package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Getter
public class InMemoryUserStorage implements UserStorage {

    private static final Map<Long, User> users = new HashMap<>();

    public static void clear() {
        users.clear();
    }

    @Override
    public User create(User user) {
        try {
            checkUserValidation(user);
        } catch (ValidationUserException e) {
            log.warn("Ошибка валидации при создании пользователя: {}", e.getMessage());
            throw e;
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с id={} успешно добавлен", user.getId());
        return user;
    }

    private static long getNextId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private static void checkUserValidation(User user) {
        String userValidation = "Ok";
        if (user.getBirthday() == null) {
            userValidation = "Запрос не полный, отсутствует дата рождения";
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            userValidation = "Электронная почта не может быть пустой и должна содержать символ @";
        }
        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            userValidation = "Логин не может быть пустым и содержать пробелы";
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            userValidation = "Дата рождения не может быть в будущем";
        }
        if (!userValidation.equals("Ok")) {
            throw new ValidationUserException(userValidation);
        }
    }

    @Override
    public User update(User user) {
        User currectedUser;
        try {
            if (user.getId() == null) {
                throw new ValidationUserException("Id должен быть указан");
            }
            Optional<User> searchUser = users.values().stream()
                    .filter(u -> Objects.equals(u.getId(), user.getId()))
                    .findFirst();
            if (searchUser.isEmpty()) {
                throw new UserNotFoundException("Пользователь с id=" + user.getId() + " не найден");
            }
            currectedUser = searchUser.get();
            checkUserValidation(user);
        } catch (ValidationUserException | UserNotFoundException e) {
            log.warn("Ошибка валидации при изменение данных пользователя: {}", e.getMessage());
            throw e;
        }
        currectedUser.setEmail(user.getEmail());
        currectedUser.setLogin(user.getLogin());
        currectedUser.setName(user.getName());
        currectedUser.setBirthday(user.getBirthday());
        log.info("Данные пользователя с id={} успешно изменены", user.getId());
        users.put(currectedUser.getId(), currectedUser);
        return currectedUser;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }
}
