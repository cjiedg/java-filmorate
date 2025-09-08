package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Component
@Primary
public class UserDbStorage extends BaseRepository<User> {

    // SQL запросы для пользователей
    private static final String FIND_ALL_SQL = "SELECT * FROM users";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_USER_SQL = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE user_id = ?";

    // SQL запросы для друзей
    private static final String GET_USER_FRIENDS_SQL = "SELECT friend_id, status FROM user_friends WHERE user_id = ?";
    private static final String GET_FRIENDS_FOR_USERS_SQL = "SELECT user_id, friend_id, status FROM user_friends WHERE user_id IN (%s)";
    private static final String ADD_FRIEND_SQL = "INSERT INTO user_friends (user_id, friend_id, status) VALUES (?, ?, ?)";
    private static final String UPDATE_FRIEND_STATUS_SQL = "UPDATE user_friends SET status = ? WHERE user_id = ? AND friend_id = ?";
    private static final String REMOVE_FRIEND_SQL = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_USER_FRIENDS_SQL = "DELETE FROM user_friends WHERE user_id = ? OR friend_id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        super(jdbcTemplate, userRowMapper);
    }

    public Collection<User> findAll() {
        List<User> users = super.findAll(FIND_ALL_SQL);
        loadFriendsForUsers(users);
        return users;
    }

    public User create(User user) {
        Long userId = super.insert(INSERT_USER_SQL,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        user.setId(userId);
        return getUserById(userId).orElse(user);
    }

    public User update(User newUser, User oldUser) {
        super.update(UPDATE_USER_SQL,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                oldUser.getId());

        return getUserById(oldUser.getId()).orElse(newUser);
    }

    public Optional<User> getUserById(long id) {
        Optional<User> userOptional = super.findById(FIND_BY_ID_SQL, id);
        userOptional.ifPresent(this::loadUserFriends);
        return userOptional;
    }

    public void delete(long id) {
        jdbcTemplate.update(DELETE_USER_FRIENDS_SQL, id, id);
        super.delete(DELETE_USER_SQL, id);
    }

    private void loadUserFriends(User user) {
        Map<Long, Boolean> friends = new HashMap<>();

        jdbcTemplate.query(GET_USER_FRIENDS_SQL, rs -> {
            Long friendId = rs.getLong("friend_id");
            Boolean status = rs.getBoolean("status");
            friends.put(friendId, status);
        }, user.getId());

        user.getFriends().clear();
        user.getFriends().putAll(friends);
    }

    private void loadFriendsForUsers(List<User> users) {
        if (users.isEmpty()) return;

        String userIds = users.stream()
                .map(u -> String.valueOf(u.getId()))
                .collect(Collectors.joining(","));

        String sql = String.format(GET_FRIENDS_FOR_USERS_SQL, userIds);
        Map<Long, Map<Long, Boolean>> userFriendsMap = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long userId = rs.getLong("user_id");
            Long friendId = rs.getLong("friend_id");
            Boolean status = rs.getBoolean("status");

            userFriendsMap.computeIfAbsent(userId, k -> new HashMap<>())
                    .put(friendId, status);
        });

        users.forEach(user -> {
            Map<Long, Boolean> friends = userFriendsMap.getOrDefault(user.getId(), new HashMap<>());
            user.getFriends().clear();
            user.getFriends().putAll(friends);
        });
    }

    public void addFriend(long userId, long friendId) {
        boolean friendshipExists = checkFriendshipExists(userId, friendId);

        if (!friendshipExists) {
            jdbcTemplate.update(ADD_FRIEND_SQL, userId, friendId, false);
        }
    }

    public void confirmFriend(long userId, long friendId) {
        jdbcTemplate.update(UPDATE_FRIEND_STATUS_SQL, true, userId, friendId);
        jdbcTemplate.update(ADD_FRIEND_SQL, friendId, userId, true);
    }

    public void removeFriend(long userId, long friendId) {
        jdbcTemplate.update(REMOVE_FRIEND_SQL, userId, friendId);
        jdbcTemplate.update(REMOVE_FRIEND_SQL, friendId, userId);
    }

    private boolean checkFriendshipExists(long userId, long friendId) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_friends WHERE user_id = ? AND friend_id = ?",
                    Integer.class, userId, friendId);
            return count != null && count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public List<User> getCommonFriends(long userId1, long userId2) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN user_friends uf1 ON u.user_id = uf1.friend_id " +
                "JOIN user_friends uf2 ON u.user_id = uf2.friend_id " +
                "WHERE uf1.user_id = ? AND uf2.user_id = ? AND uf1.status = true AND uf2.status = true";

        List<User> commonFriends = jdbcTemplate.query(sql, this.rowMapper, userId1, userId2);
        loadFriendsForUsers(commonFriends);
        return commonFriends;
    }
}