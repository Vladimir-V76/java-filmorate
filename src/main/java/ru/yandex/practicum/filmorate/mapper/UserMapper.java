package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.ConfirmFriendDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setLogin(user.getLogin());
        userDto.setName(user.getName());
        userDto.setBirthday(user.getBirthday());
        userDto.setConfirmFriends(
                UserMapper.mapToConfirmFriendDto(user.getConfirmFriends().values().stream().toList())
        );

        return userDto;
    }

    public static List<ConfirmFriendDto> mapToConfirmFriendDto(List<ConfirmFriend> confirmFriend) {
        List<ConfirmFriendDto> dto = new ArrayList<>();
        confirmFriend.forEach(f -> {
            ConfirmFriendDto d = new ConfirmFriendDto();
            d.setFriendId(f.getFriendId());
            d.setConfirmation(f.isConfirmation());
            dto.add(d);
        });
        return dto;
    }

    public static User mapToUserFormNewUserRequest(NewUserRequest newUser) {
        User user = new User();
        user.setEmail(newUser.getEmail());
        user.setLogin(newUser.getLogin());
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            user.setName(newUser.getLogin());
        } else {
            user.setName(newUser.getName());
        }
        user.setBirthday(newUser.getBirthday());
        user.setFriendsSet(newUser.getFriendsSet());
        if (!newUser.getConfirmFriends().isEmpty()) {
            Map<Long, ConfirmFriend> confirmFriendMap = newUser.getConfirmFriends().stream()
                    .collect(Collectors.toMap(ConfirmFriend::getFriendId, Function.identity()));
            user.setConfirmFriends(confirmFriendMap);
        }
        return user;
    }

    public static User mapToUserFromUpdateUserRequest(UpdateUserRequest updateUser) {
        User user = new User();
        user.setId(updateUser.getId());
        user.setEmail(updateUser.getEmail());
        user.setLogin(updateUser.getLogin());
        if (updateUser.getName() == null || updateUser.getName().isBlank()) {
            user.setName(updateUser.getLogin());
        } else {
            user.setName(updateUser.getName());
        }
        user.setBirthday(updateUser.getBirthday());
        user.setConfirmFriends(updateUser.getConfirmFriends().stream()
                .collect(Collectors.toMap(ConfirmFriend::getFriendId, Function.identity())));

        return user;
    }

    public static UpdateUserRequest mapToUpdateUserRequest(User user) {
        UpdateUserRequest updateUser = new UpdateUserRequest();
        updateUser.setId(updateUser.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setLogin(user.getLogin());
        updateUser.setName(user.getName());
        updateUser.setBirthday(user.getBirthday());
        updateUser.setConfirmFriends(user.getConfirmFriends().values().stream().toList());

        return updateUser;
    }

    public static User updateUserFields(User user, UpdateUserRequest updateUser) {
        if (updateUser.hasId()) {
            user.setId(updateUser.getId());
        }
        if (updateUser.hasEmail()) {
            user.setEmail(updateUser.getEmail());
        }
        if (updateUser.hasLogin()) {
            user.setLogin(updateUser.getLogin());
        }
        if (updateUser.hasName()) {
            user.setName(updateUser.getName());
        }
        if (updateUser.hasBirthday()) {
            user.setBirthday(updateUser.getBirthday());
        }
        if (updateUser.hasConfirmFriends()) {
            user.setConfirmFriends(updateUser.getConfirmFriends().stream()
                    .collect(Collectors.toMap(ConfirmFriend::getFriendId, Function.identity())));
        }
        return user;
    }

}
