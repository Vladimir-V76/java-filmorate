package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateUserRequest {
    @NotNull(message = "Id должен быть указан")
    @Min(value = 1, message = "Id должен быть числом положительным")
    private Long id;
    @Email(message = "Электронная почта должна содержать символ @")
    private String email;
    private String login;
    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private List<ConfirmFriend> confirmFriends = new ArrayList<>();

    public boolean hasId() {
        return !(id == null);
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasBirthday() {
        return !(birthday == null);
    }

    public boolean hasConfirmFriends() {
        return !(confirmFriends == null || confirmFriends.isEmpty());
    }
}
