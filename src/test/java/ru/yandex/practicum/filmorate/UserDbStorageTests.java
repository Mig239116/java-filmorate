package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserDbStorageTests {
    private final UserDbStorage userStorage;

    @Test
    public void checkCreateUser() {
        User user = new User();
        user.setName("name");
        user.setBirthday(LocalDate.of(1983, 4, 5));
        user.setLogin("login");
        user.setEmail("mail@mail.ru");
        userStorage.addUser(user);
        assertThat(user).hasFieldOrPropertyWithValue("email", "mail@mail.ru");
        assertThat(user).hasFieldOrPropertyWithValue("login", "login");
        assertThat(user).hasFieldOrPropertyWithValue("name", "name");
        assertThat(user).hasFieldOrProperty("birthday");
    }

    @Test
    public void checkUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setBirthday(LocalDate.of(1983, 4, 5));
        user.setLogin("login");
        user.setEmail("mail@mail.ru");
        userStorage.updateUser(user);
        assertThat(user).hasFieldOrPropertyWithValue("email", "mail@mail.ru");
        assertThat(user).hasFieldOrPropertyWithValue("login", "login");
        assertThat(user).hasFieldOrPropertyWithValue("name", "name");
        assertThat(user).hasFieldOrProperty("birthday");
    }

    @Test
    public void checkGetAllUsers() {
        assertEquals(userStorage.getAllUsers().size() , 3);
        User user = userStorage.getAllUsers().stream().toList().get(0);
        assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(user).hasFieldOrPropertyWithValue("email", "mail1@mail.ru");
        assertThat(user).hasFieldOrPropertyWithValue("login", "login1");
        assertThat(user).hasFieldOrPropertyWithValue("name", "name1");
        assertThat(user).hasFieldOrProperty("birthday");
    }

    @Test
    public void checkAddDeleteFriend() {
        userStorage.addFriend(1L, 2L);
        List<User> friends = userStorage.getAllFriends(1L).stream().toList();
        assertEquals(1, friends.size());
        assertEquals(2, friends.get(0).getId());
        userStorage.deleteFriend(1L, 2L);
        friends = userStorage.getAllFriends(1L).stream().toList();
        assertEquals(0, friends.size());
    }

    @Test
    public void checkCommonFriends() {
        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(1L, 3L);
        userStorage.addFriend(2L, 3L);
        List<User> friends = userStorage.getCommonFriends(1L, 2L).stream().toList();
        assertEquals(1, friends.size());
        assertEquals(3, friends.get(0).getId());

    }

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }
}
