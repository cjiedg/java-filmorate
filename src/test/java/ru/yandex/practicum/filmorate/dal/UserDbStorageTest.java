package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Test
    public void testFindAll_WhenNoUsers() {
        Collection<User> users = userDbStorage.findAll();
        assertThat(users).isNotNull().isEmpty();
    }

    @Test
    public void testFindAll_WithUsers() {
        User user1 = createAndSaveUser("test1@email.com", "testlogin1", "Test User 1");
        User user2 = createAndSaveUser("test2@email.com", "testlogin2", "Test User 2");

        Collection<User> users = userDbStorage.findAll();

        assertThat(users)
                .hasSize(2)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("test1@email.com", "test2@email.com");
    }

    @Test
    public void testCreateUser() {
        User newUser = createTestUser("test@email.com", "testlogin", "Test User");

        User createdUser = userDbStorage.create(newUser);

        assertThat(createdUser)
                .isNotNull()
                .satisfies(user -> {
                    assertThat(user.getId()).isPositive();
                    assertThat(user.getEmail()).isEqualTo(newUser.getEmail());
                    assertThat(user.getLogin()).isEqualTo(newUser.getLogin());
                    assertThat(user.getName()).isEqualTo(newUser.getName());
                });

        assertThat(userDbStorage.getUserById(createdUser.getId())).isPresent();
    }

    @Test
    public void testUpdateUser() {
        User originalUser = createAndSaveUser("original@email.com", "original", "Original User");

        User updatedUserData = createTestUser("updated@email.com", "updated", "Updated User");
        updatedUserData.setId(originalUser.getId());

        User updatedUser = userDbStorage.update(updatedUserData, originalUser);

        assertThat(updatedUser)
                .isNotNull()
                .satisfies(user -> {
                    assertThat(user.getId()).isEqualTo(originalUser.getId());
                    assertThat(user.getEmail()).isEqualTo(updatedUserData.getEmail());
                });
    }

    @Test
    public void testGetUserById_WhenUserExists() {
        User testUser = createAndSaveUser("test@email.com", "testuser", "Test User");

        Optional<User> foundUser = userDbStorage.getUserById(testUser.getId());

        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(testUser.getId());
                    assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
                });
    }

    @Test
    public void testGetUserById_WhenUserNotExists() {
        Optional<User> foundUser = userDbStorage.getUserById(999L);
        assertThat(foundUser).isNotPresent();
    }

    @Test
    public void testDeleteUser() {
        User testUser = createAndSaveUser("delete@email.com", "todelete", "To Delete");

        assertThat(userDbStorage.getUserById(testUser.getId())).isPresent();
        userDbStorage.delete(testUser.getId());
        assertThat(userDbStorage.getUserById(testUser.getId())).isNotPresent();
    }

    @Test
    public void testAddFriend() {
        User user1 = createAndSaveUser("user1@email.com", "user1", "User One");
        User user2 = createAndSaveUser("user2@email.com", "user2", "User Two");

        userDbStorage.addFriend(user1.getId(), user2.getId());

        Optional<User> userWithFriend = userDbStorage.getUserById(user1.getId());
        assertThat(userWithFriend)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getFriends()).containsKey(user2.getId())
                );
    }

    @Test
    public void testConfirmFriend() {
        User user1 = createAndSaveUser("user1@email.com", "user1", "User One");
        User user2 = createAndSaveUser("user2@email.com", "user2", "User Two");

        userDbStorage.addFriend(user1.getId(), user2.getId());
        userDbStorage.confirmFriend(user2.getId(), user1.getId());

        Optional<User> user1WithFriends = userDbStorage.getUserById(user1.getId());
        assertThat(user1WithFriends)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getFriends().get(user2.getId())).isTrue()
                );
    }

    @Test
    public void testRemoveFriend() {
        User user1 = createAndSaveUser("user1@email.com", "user1", "User One");
        User user2 = createAndSaveUser("user2@email.com", "user2", "User Two");

        userDbStorage.addFriend(user1.getId(), user2.getId());

        Optional<User> userBeforeRemoval = userDbStorage.getUserById(user1.getId());
        assertThat(userBeforeRemoval)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getFriends()).containsKey(user2.getId())
                );

        userDbStorage.removeFriend(user1.getId(), user2.getId());

        Optional<User> userAfterRemoval = userDbStorage.getUserById(user1.getId());
        assertThat(userAfterRemoval)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getFriends()).doesNotContainKey(user2.getId())
                );
    }

    @Test
    public void testGetCommonFriends() {
        User user1 = createAndSaveUser("user1@email.com", "user1", "User One");
        User user2 = createAndSaveUser("user2@email.com", "user2", "User Two");
        User commonFriend = createAndSaveUser("common@email.com", "common", "Common Friend");

        
        userDbStorage.addFriend(user1.getId(), commonFriend.getId());
        userDbStorage.addFriend(user2.getId(), commonFriend.getId());

        
        userDbStorage.confirmFriend(commonFriend.getId(), user1.getId());
        userDbStorage.confirmFriend(commonFriend.getId(), user2.getId());

        List<User> commonFriends = userDbStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(commonFriend.getId());
    }


    @Test
    public void testGetCommonFriends_WhenNoCommonFriends() {
        User user1 = createAndSaveUser("user1@email.com", "user1", "User One");
        User user2 = createAndSaveUser("user2@email.com", "user2", "User Two");

        List<User> commonFriends = userDbStorage.getCommonFriends(user1.getId(), user2.getId());
        assertThat(commonFriends).isEmpty();
    }

    private User createTestUser(String email, String login, String name) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.now().minusYears(20));
        return user;
    }

    private User createAndSaveUser(String email, String login, String name) {
        User user = createTestUser(email, login, name);
        return userDbStorage.create(user);
    }
}