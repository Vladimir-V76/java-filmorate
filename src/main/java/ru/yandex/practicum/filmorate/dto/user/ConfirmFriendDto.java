package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

@Data
public class ConfirmFriendDto {
    Long friendId;
    boolean confirmation;
}

