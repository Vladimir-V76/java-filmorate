package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private List<ConfirmFriendDto> ConfirmFriends = new ArrayList<>();
}
