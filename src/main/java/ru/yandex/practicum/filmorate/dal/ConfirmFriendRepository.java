package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;

import java.util.List;

@Repository
public class ConfirmFriendRepository extends BaseRepository<ConfirmFriend> {

    public static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM friends WHERE user_id = ?";
    public static final String INSERT_QUERY = "INSERT INTO friends (user_id, friend_id, is_confirmed) VALUES (?, ?, ?)";
    public static final String DELETE_QUERY = "DELETE FROM friends WHERE user_id = ?";

    public ConfirmFriendRepository(JdbcTemplate jdbc, RowMapper<ConfirmFriend> mapper) {
        super(jdbc, mapper);
    }

    public List<ConfirmFriend> findById(long userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }

    public void create(ConfirmFriend confirmFriend, long userId) {
        insert(
                false,
                INSERT_QUERY,
                userId,
                confirmFriend.getFriendId(),
                confirmFriend.isConfirmation()
        );
    }

    public void delete(Long id) {
        delete(
                DELETE_QUERY,
                id
        );
    }
}

