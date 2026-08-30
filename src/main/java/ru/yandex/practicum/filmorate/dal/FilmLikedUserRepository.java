package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.film.FilmLikedUser;

import java.util.List;

@Repository
public class FilmLikedUserRepository extends BaseRepository<FilmLikedUser> {

    public static final String FIND_BY_ID_QUERY = "SELECT * FROM liked_film_user WHERE film_id = ?";
    public static final String INSERT_QUERY = "INSERT INTO liked_film_user (film_id, user_id) VALUES (?, ?)";
    public static final String DELETE_QUERY = "DELETE FROM liked_film_user WHERE film_id = ?";

    public FilmLikedUserRepository(JdbcTemplate jdbc, RowMapper<FilmLikedUser> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmLikedUser> findById(long filmId) {
        return findMany(FIND_BY_ID_QUERY, filmId);
    }

    public void create(Long filmId, Long userId) {
        insert(
                false,
                INSERT_QUERY,
                filmId,
                userId
        );
    }

    public void delete(Long filmId) {
        delete(
                DELETE_QUERY,
                filmId
        );
    }
}
