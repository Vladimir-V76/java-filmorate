package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmLikedUser;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmLikedUserRowMapper implements RowMapper<FilmLikedUser> {

    @Override
    public FilmLikedUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmLikedUser filmLikedUser = new FilmLikedUser();
        filmLikedUser.setFilmId(rs.getLong("film_id"));
        filmLikedUser.setUserId(rs.getLong("user_id"));
        return filmLikedUser;
    }
}
