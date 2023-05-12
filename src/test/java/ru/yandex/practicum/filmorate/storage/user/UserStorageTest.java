package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserStorageTest {
    private final UserStorage userStorage;

    @Test
    void getUser() {
        User user = getUser("1");
        User actUser = userStorage.createUser(user);
        actUser = userStorage.getUser(actUser.getId());
        assertEquals(user.getId(), actUser.getId());
        assertEquals(user.getName(), actUser.getName());
    }

    @Test
    void getUsers() {
        userStorage.deleteAll();
        User user1 = getUser("2");
        User user2 = getUser("3");
        userStorage.createUser(user1);
        userStorage.createUser(user2);
        List<User> expUsers = List.of(user1, user2);

        Collection<User> actUsers = userStorage.getUsers();
        List<User> actUsersList = new ArrayList<>(actUsers);

        assertEquals(expUsers, actUsersList);
        assertEquals(2, actUsersList.size());
    }

    @Test
    void createUser() {
        User user = getUser("4");
        User actUser = userStorage.createUser(user);
        actUser = userStorage.getUser(actUser.getId());
        assertEquals(user.getName(), actUser.getName());
        assertEquals(user.getEmail(), actUser.getEmail());
        assertEquals(user.getLogin(), actUser.getLogin());
        assertEquals(user.getBirthday(), actUser.getBirthday());

    }

    @Test
    void updateUser() {
        User user = userStorage.createUser(getUser("5"));
        user.setName("Super Sus");
        userStorage.updateUser(user);
        User actUser = userStorage.getUser(user.getId());
        assertEquals("Super Sus", actUser.getName());

    }

    @Test
    void addFriend() {
        userStorage.deleteAll();
        User user1 = userStorage.createUser(getUser("5"));
        User user2 = userStorage.createUser(getUser("6"));

        List<User> questOne = userStorage.getFriends(user1.getId());
        assertTrue(questOne.isEmpty());

        userStorage.addFriend(user1.getId(), user2.getId());
        List<User> questTwo = userStorage.getFriends(user1.getId());
        assertTrue(questTwo.contains(user2));

        List<User> questThree = userStorage.getFriends(user2.getId());
        assertTrue(questThree.isEmpty());
        userStorage.addFriend(user2.getId(), user1.getId());
        questThree = userStorage.getFriends(user2.getId());
        assertTrue(questThree.contains(user1));

    }

    @Test
    void removeFriend() {
        userStorage.deleteAll();
        User user1 = userStorage.createUser(getUser("6"));
        User user2 = userStorage.createUser(getUser("7"));

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.deleteFriend(user1.getId(), user2.getId());
        List<User> quest = userStorage.getFriends(user1.getId());
        assertTrue(quest.isEmpty());

    }

    private User getUser(String name) {
        User user = new User();
        user.setEmail("dmitry@pidorya.ru");
        user.setLogin("syb");
        user.setName(name);
        user.setBirthday(LocalDate.of(1997, 2, 28));
        return user;
    }

}