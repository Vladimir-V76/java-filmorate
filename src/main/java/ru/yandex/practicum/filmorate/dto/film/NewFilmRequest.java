package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewFilmRequest {

    @NotBlank(message = "Название не должно быть пустым")
    private String name;

    private String description;

    @NotNull(message = "Дата релиза обязательна")
    private LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    private List<FilmGenreDto> genres;
    private FilmMpaDto mpa;
}