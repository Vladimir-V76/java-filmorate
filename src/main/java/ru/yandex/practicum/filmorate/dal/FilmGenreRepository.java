package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.FilmGenre;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmGenreRepository extends BaseRepository<FilmGenre> {

    public static final String FIND_BY_ID_QUERY =
            "SELECT * FROM film_genre fg JOIN genre g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
    public static final String INSERT_QUERY =
            "MERGE INTO film_genre (film_id, genre_id) KEY (film_id, genre_id) VALUES (?, ?)";
    public static final String FIND_BY_GENRE_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";
    public static final String DELETE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    public static final String FIND_ALL_QUERY = "SELECT * FROM genre";

    public FilmGenreRepository(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmGenre> findByFilmId(long filmId) {
        return findMany(FIND_BY_ID_QUERY, filmId);
    }

    public Optional<FilmGenre> findByGenreId(Long id) {
        return findOne(FIND_BY_GENRE_ID_QUERY, id);
    }

    public void create(FilmGenre filmGenre, Long filmId) {
        insert(
                false,
                INSERT_QUERY,
                filmId,
                filmGenre.getId()
        );
    }

    public void delete(Long filmId) {
        delete(
                DELETE_QUERY,
                filmId
        );
    }

    public List<FilmGenre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }
}
