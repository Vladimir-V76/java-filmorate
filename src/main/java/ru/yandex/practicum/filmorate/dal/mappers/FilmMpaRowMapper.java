package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.FilmMpa;
import ru.yandex.practicum.filmorate.model.film.FilmMpaEnum;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmMpaRowMapper implements RowMapper<FilmMpa> {
    @Override
    public FilmMpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmMpa filmMpa = new FilmMpa();
        filmMpa.setId(rs.getLong("mpa_id"));
        filmMpa.setName(FilmMpaEnum.from(rs.getString("mpa_name")));

        return filmMpa;
    }
}
