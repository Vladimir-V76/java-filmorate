package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;

@Data
public class ConfirmFriend {
    Long friendId;
    boolean confirmation;

}
