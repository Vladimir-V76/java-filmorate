package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.FilmMpa;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmMpaRepository extends BaseRepository<FilmMpa> {

    public static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = ?";
    public static final String FIND_ALL_QUERY = "SELECT * FROM mpa";

    public FilmMpaRepository(JdbcTemplate jdbc, RowMapper<FilmMpa> mapper) {
        super(jdbc, mapper);
    }

    public Optional<FilmMpa> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<FilmMpa> findAll() {
        return findMany(FIND_ALL_QUERY);
    }
}
