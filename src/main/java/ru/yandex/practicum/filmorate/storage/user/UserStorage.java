package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    User getUser(int id);

    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    List<User> getFriend(int id);

    List<User> getCommonFriends(int id, int otherId);
}
