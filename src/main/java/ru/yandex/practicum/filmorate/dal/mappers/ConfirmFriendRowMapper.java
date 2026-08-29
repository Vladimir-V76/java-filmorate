package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.ConfirmFriend;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ConfirmFriendRowMapper implements RowMapper<ConfirmFriend> {
    @Override
    public ConfirmFriend mapRow(ResultSet rs, int rowNum) throws SQLException {
       ConfirmFriend confirmFriend = new ConfirmFriend();
       confirmFriend.setFriendId(rs.getLong("friend_id"));
       confirmFriend.setConfirmation(rs.getBoolean("is_confirmed"));
    return confirmFriend;
    }
}
