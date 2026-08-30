package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository extends BaseRepository<User> {

    public static final String FIND_ALL_QUERY = "SELECT * FROM users";
    public static final String FIND_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    public static final String INSERT_QUERY =
            "INSERT INTO users (email, login, name, birthday_date) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_QUERY =
            "UPDATE users SET email = ?, login = ?, name = ?, birthday_date = ? WHERE user_id = ?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID, userId);
    }

    public User create(User user) {
        long id = insert(
                true,
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return user;
    }
}
