package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(of = {"email"})
public class User {
        private Long id;
        private String email;
        private String login;
        private String name;
        private LocalDate birthday;
        private Set<Long> friendsSet = new HashSet<>(); //подтвержденные пользователи
        private Map<Long, ConfirmFriend> ConfirmFriends = new HashMap<>();
}
