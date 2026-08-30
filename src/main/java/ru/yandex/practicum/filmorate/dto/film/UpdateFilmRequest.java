package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateFilmRequest {
    @NotNull(message = "Id должен быть указан.")
    @Min(value = 1, message = "Id должно быть числом положительным")
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private List<FilmGenreDto> genres;
    private FilmMpaDto mpa;

    public boolean hasId() {
        return !(id == null);
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasDuration() {
        return duration > 0;
    }

    public boolean hasGenres() {
        return !(genres == null || genres.isEmpty());
    }

    public boolean hasMpa() {
        return !(mpa == null);
    }
}