package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;

@Data
public class FilmLikedUser {
    private Long filmId;
    private Long userId;
}
