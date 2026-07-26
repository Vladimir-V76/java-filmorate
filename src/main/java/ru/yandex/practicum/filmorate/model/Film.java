package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    @EqualsAndHashCode.Exclude
    private Set<Long> likedFilm = new HashSet<>();

    private List<FilmGenre> genre;
    private FilmRating rating;
}
