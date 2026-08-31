package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    User findUserById(Long id);
}