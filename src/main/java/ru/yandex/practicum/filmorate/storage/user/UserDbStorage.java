package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.ConfirmFriendRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component("inDbUserStorage")
public class UserDbStorage implements UserStorage {
    private final UserRepository userRepository;
    private final ConfirmFriendRepository confirmFriendRepository;

    public UserDbStorage(UserRepository userRepository, ConfirmFriendRepository confirmFriendRepository) {
        this.userRepository = userRepository;
        this.confirmFriendRepository = confirmFriendRepository;
    }

    @Override
    public User create(User user) {
        user = userRepository.create(user);
        List<ConfirmFriend> confirmFriend = user.getConfirmFriends().values().stream().toList();
        if (!(confirmFriend.isEmpty())) {
            long userId = user.getId();
            confirmFriend.forEach(cf -> confirmFriendRepository.create(cf, userId));
        }

        return user;
    }

    @Override
    public User update(User user) {
        UpdateUserRequest updateUser = UserMapper.mapToUpdateUserRequest(user);
        long userId = user.getId();
        User updatedUser = userRepository.findById(userId)
                .map(u -> UserMapper.updateUserFields(u, updateUser))
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден. ID: " + userId));

        userRepository.update(updatedUser);
        confirmFriendRepository.delete(userId);
        if (!user.getConfirmFriends().isEmpty()) {
            user.getConfirmFriends().values()
                    .forEach(cf -> confirmFriendRepository.create(cf, userId));

        }
        return updatedUser;
    }

    @Override
    public Collection<User> findAll() {
        Collection<User> users = userRepository.findAll();
        users.forEach(u -> u.setConfirmFriends(findListConfirmedFriendsByUserId(u.getId())));
        users.forEach(u -> u.setFriendsSet(getFriendsSetFromMapConfirmFriends(u.getConfirmFriends())));

        return users;
    }

    @Override
    public void delete(User user) {

    }

    @Override
    public User findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден. ID: " + userId));
        user.setConfirmFriends(findListConfirmedFriendsByUserId(user.getId()));
        user.setFriendsSet(getFriendsSetFromMapConfirmFriends(user.getConfirmFriends()));

        return user;
    }

    private Map<Long, ConfirmFriend> findListConfirmedFriendsByUserId(long userId) {
        return confirmFriendRepository.findById(userId).stream()
                .collect(Collectors.toMap(ConfirmFriend::getFriendId, Function.identity()));
    }

    private static Set<Long> getFriendsSetFromMapConfirmFriends(Map<Long, ConfirmFriend> confirmFriends) {
        return confirmFriends.values().stream()
                .filter(ConfirmFriend::isConfirmation)
                .map(ConfirmFriend::getFriendId)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
