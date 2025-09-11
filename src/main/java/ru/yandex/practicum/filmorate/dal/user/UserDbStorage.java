package ru.yandex.practicum.filmorate.dal.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private static final String FIND_ALL_SQL = "SELECT * FROM users";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_USER_SQL = "INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE user_id = ?";
    private static final String GET_FRIENDS_SQL = "SELECT friend_id, status FROM friendship WHERE user_id = ?";

    @Getter
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper rowMapper;

    @Override
    public Collection<User> findAll() {
        List<User> users = jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
        loadFriendsForUsers(users);
        return users;
    }

    @Override
    public Optional<User> getUserById(long id) {
        Optional<User> userOptional = jdbcTemplate.query(FIND_BY_ID_SQL, rowMapper, id)
                .stream()
                .findFirst();
        userOptional.ifPresent(this::loadUserFriends);
        return userOptional;
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_USER_SQL,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) throw new RuntimeException("Не удалось получить ID пользователя");

        user.setId(key.longValue());
        user.getFriends().clear();
        return user;
    }

    @Override
    public User update(User user) {
        int updated = jdbcTemplate.update(UPDATE_USER_SQL,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        if (updated == 0) throw new NotFoundException("User not found: " + user.getId());
        return getUserById(user.getId()).orElseThrow(() -> new NotFoundException("User not found after update: " + user.getId()));
    }

    @Override
    public void delete(User user) {
        jdbcTemplate.update(DELETE_USER_SQL, user.getId());
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email
        );
        return count != null && count > 0;
    }

    @Override
    public void addFriend(long userId, long friendId) {

        if (friendshipExists(userId, friendId)) {
            jdbcTemplate.update("UPDATE friendship SET status = ? WHERE user_id = ? AND friend_id = ?", true, userId, friendId);
        } else {
            jdbcTemplate.update("INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?)", userId, friendId, true);
        }
    }

    @Override
    public void confirmFriend(long userId, long friendId) {

        if (friendshipExists(friendId, userId)) {

            jdbcTemplate.update("UPDATE friendship SET status = ? WHERE user_id = ? AND friend_id = ?", true, friendId, userId);

            if (friendshipExists(userId, friendId)) {
                jdbcTemplate.update("UPDATE friendship SET status = ? WHERE user_id = ? AND friend_id = ?", true, userId, friendId);
            } else {
                jdbcTemplate.update("INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?)", userId, friendId, true);
            }
        } else {

            if (friendshipExists(userId, friendId)) {
                jdbcTemplate.update("UPDATE friendship SET status = ? WHERE user_id = ? AND friend_id = ?", true, userId, friendId);
            } else {
                jdbcTemplate.update("INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?)", userId, friendId, true);
            }
        }
    }

    @Override
    public void removeFriend(long userId, long friendId) {

        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?", userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(long userId1, long userId2) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendship f1 ON u.user_id = f1.friend_id AND f1.user_id = ? AND f1.status = true " +
                "JOIN friendship f2 ON u.user_id = f2.friend_id AND f2.user_id = ? AND f2.status = true";

        List<User> commonFriends = jdbcTemplate.query(sql, rowMapper, userId1, userId2);
        loadFriendsForUsers(commonFriends);
        return commonFriends;
    }

    public Set<Long> getFriendsIds(long userId) {
        List<Long> ids = jdbcTemplate.query(
                "SELECT friend_id FROM friendship WHERE user_id = ? AND status = TRUE",
                (rs, rowNum) -> rs.getLong("friend_id"),
                userId
        );
        return new HashSet<>(ids);
    }


    private void loadUserFriends(User user) {
        Map<Long, Boolean> friends = jdbcTemplate.query(
                GET_FRIENDS_SQL,
                (rs, rowNum) -> Map.entry(rs.getLong("friend_id"), rs.getBoolean("status")),
                user.getId()
        ).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        user.getFriends().clear();
        user.getFriends().putAll(friends);
    }


    private void loadFriendsForUsers(List<User> users) {
        for (User user : users) loadUserFriends(user);
    }

    private boolean friendshipExists(long userId, long friendId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?", Integer.class, userId, friendId
        );
        return count != null && count > 0;
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendship f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? AND f.status = true";

        List<User> friends = jdbcTemplate.query(sql, rowMapper, userId);
        loadFriendsForUsers(friends);
        return friends;
    }

}
