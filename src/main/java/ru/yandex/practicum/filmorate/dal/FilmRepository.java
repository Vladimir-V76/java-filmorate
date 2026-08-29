package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film>{

    public static final String FIND_ALL_QUERY = "SELECT * FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";
    public static final String FIND_BY_ID_QUERY =
            "SELECT * FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id = ?";
    public static final String INSERT_QUERY =
            "INSERT INTO films (film_name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_QUERY = "UPDATE films SET film_name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE film_id = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY); }

    public Optional<Film> findById(Long filmId) { return findOne(FIND_BY_ID_QUERY, filmId); }

    public Film create(Film film, Long mpaId) {
        Long mpaStatus = mpaId >0 ? mpaId : null;
        long id = insert(
                true,
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaStatus
        );
        film.setId(id);
        return film;
    }

    public Film update(Film film, Long mpaId) {
        Long mpaStatus = mpaId >0 ? mpaId : null;
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaStatus,
                film.getId()
        );

        return film;
    }
}
