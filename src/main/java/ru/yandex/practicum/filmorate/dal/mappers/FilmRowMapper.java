package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.FilmMpa;
import ru.yandex.practicum.filmorate.model.film.FilmMpaEnum;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("description"));

        Timestamp releaseDate = rs.getTimestamp("release_date");
        film.setReleaseDate(LocalDate.ofInstant(releaseDate.toInstant(), ZoneId.systemDefault()));

        film.setDuration(rs.getInt("duration"));

        FilmMpa mpa = new FilmMpa();

        mpa.setId(rs.getLong("mpa_id"));
        mpa.setName(FilmMpaEnum.from(rs.getString("mpa_name")));
        if (mpa.getId() != 0) {
            film.setMpa(mpa);
        }
        return film;
    }
}
