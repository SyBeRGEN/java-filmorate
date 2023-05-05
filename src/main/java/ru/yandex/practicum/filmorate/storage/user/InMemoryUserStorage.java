package ru.yandex.practicum.filmorate.storage.user;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FriendsException;
import ru.yandex.practicum.filmorate.exceptions.SearchException;
import ru.yandex.practicum.filmorate.exceptions.ValidationExcpetions;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private static int userId = 1;
    private final HashMap<Integer, User> userList = new HashMap<>();
    private final HashMap<Integer, HashSet<Integer>> userFriends = new HashMap<>();

    @Override
    public User getUser(int id) {
        if (userList.containsKey(id)) {
            log.info("Вызван метод getUsers");
            return userList.get(id);
        }
        throw new SearchException("Пользователь с данным Id не найден" + id);
    }

    @Override
    public Collection<User> getUsers() {
        log.info("Вызван метод getUsers");
        return userList.values();
    }

    @SneakyThrows
    @Override
    public User createUser(User user) {
        log.info("Вызван метод createUser");
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationExcpetions("Логин не может быть пустым или содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationExcpetions("Дата релиза — не раньше 28 декабря 1895 года");
        } else {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            user.setId(userId++);
            userList.put(user.getId(), user);
            userFriends.put(user.getId(), new HashSet<>());
            return user;
        }
    }

    @SneakyThrows
    @Override
    public User updateUser(User user) {
        log.info("Вызван метод putUser");
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationExcpetions("Логин не может быть пустым или содержать пробелы");
        } else if (!userList.containsKey(user.getId())) {
            throw new ValidationExcpetions("Нет такого Id");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationExcpetions("Дата релиза — не раньше 28 декабря 1895 года");
        } else {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            userList.put(user.getId(), user);
            return user;
        }
    }

    @Override
    public void addFriend(int id, int friendId) {
        if (userList.containsKey(id)) {
            if (userList.containsKey(friendId)) {
                userFriends.get(id).add(friendId);
                userFriends.get(friendId).add(id);
                log.info(id + " и " + friendId + " теперь друзья");
            } else throw new SearchException("Пользователь с данным Id не найден" + friendId);
        } else throw new SearchException("Пользователь с данным Id не найден" + id);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        if (userList.containsKey(id)) {
            if (userList.containsKey(friendId)) {
                if (userFriends.get(id).contains(friendId)) {
                    userFriends.get(id).remove(friendId);
                    userFriends.get(friendId).remove(id);
                    log.info(id + " и " + friendId + " больше не друзья");
                } else throw new FriendsException(id + " и " + friendId + " не являются друзьями");
            } else throw new SearchException("Пользователь с данным Id не найден" + friendId);
        } else throw new SearchException("Пользователь с данным Id не найден" + id);
    }

    @Override
    public List<User> getFriends(int id) {
        if (userList.containsKey(id)) {
            log.info("Получен список друзей пользователя " + id);
            List<User> friendList = new ArrayList<>();

            for (Integer friend : userFriends.get(id)) {
                friendList.add(userList.get(friend));
            }
            return friendList;
        } else throw new SearchException("Пользователь с данным Id не найден" + id);
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        if (userList.containsKey(id)) {
            if (userList.containsKey(otherId)) {
                List<User> friendList = new ArrayList<>();

                for (Integer integer : findCommonElements(userFriends.get(id), userFriends.get(otherId))) {
                    friendList.add(userList.get(integer));
                }
                return friendList;
            } else throw new SearchException("Пользователь с данным Id не найден" + otherId);
        } else throw new SearchException("Пользователь с данным Id не найден" + id);
    }

    private static <T> Set<T> findCommonElements(Set<T> first, Set<T> second) {
        if (first == null || second == null) {
            return new HashSet<>();
        }
        Set<T> common = new HashSet<>(first);
        common.retainAll(second);

        return common;
    }
}
