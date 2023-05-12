package ru.yandex.practicum.filmorate.storage.user;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.UserValidator;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@Primary
@Qualifier("UserDBStorage")
public class UserDBStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUser(int id) {

        String sqlUserIdList = "SELECT USER_ID FROM USERS WHERE USER_ID = ?";
        List<Integer> userIdList = jdbcTemplate.query(
                sqlUserIdList, (rs, rowNum) -> rs.getInt("USER_ID"), id);
        UserValidator.validateExist(userIdList, id);

        String sqlUserById = "SELECT * FROM USERS WHERE USER_ID = ?";
        List<User> users = jdbcTemplate.query(sqlUserById, UserDBStorage::makeUser, id);
        return users.get(0);
    }

    @Override
    public Collection<User> getUsers() {
        String sqlUserById = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlUserById, UserDBStorage::makeUser);
    }


    @Override
    public User createUser(User user) {
        log.info("Вызван метод createUser");

        String sqlUserIdList = "SELECT USER_ID FROM USERS";
        List<Integer> userIdList = jdbcTemplate.query(
                sqlUserIdList, (rs, rowNum) -> rs.getInt("USER_ID"));

        UserValidator.validateCreation(userIdList, user);
        UserValidator.validateUser(user);

        String sqlCreateUser = "INSERT INTO USERS(" +
                "LOGIN, " +
                "USER_NAME, " +
                "EMAIL, " +
                "BIRTHDAY) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlCreateUser, new String[]{"USER_ID"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return user;
    }

    @SneakyThrows
    @Override
    public User updateUser(User user) {
        log.info("Вызван метод putUser");
        String sqlUserIdList = "SELECT USER_ID FROM USERS";
        List<Integer> userIdList = jdbcTemplate.query(
                sqlUserIdList, (rs, rowNum) -> rs.getInt("USER_ID"));

        UserValidator.validateUpdate(userIdList, user);
        UserValidator.validateUser(user);

        String sqlUpdateUser = "UPDATE USERS SET " +
                "LOGIN = ?, " +
                "USER_NAME = ?, " +
                "EMAIL = ?, " +
                "BIRTHDAY = ? " +
                "WHERE USER_ID = ?";

        jdbcTemplate.update(sqlUpdateUser, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void addFriend(int id, int friendId) {
        String sqlUserIdList = "SELECT USER_ID FROM USERS";
        List<Integer> userIdList = jdbcTemplate.queryForList(sqlUserIdList, Integer.class);
        UserValidator.validateExist(userIdList, id);
        UserValidator.validateExist(userIdList, friendId);

        String sqlCreateFriendStatus = "INSERT INTO FRIENDS(" +
                "USER_ID, " +
                "FRIEND_ID) " +
                "VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlCreateFriendStatus, new String[]{"FRIENDSTATUS_ID"});
            stmt.setLong(1, id);
            stmt.setLong(2, friendId);
            return stmt;
        }, keyHolder);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        String sqlDeleteFriendStatus = "DELETE FROM FRIENDS " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlDeleteFriendStatus, id, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        log.info("Вызван метод getFriends");

        String sqlFriendsByUserId = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?";
        List<Integer> friends = jdbcTemplate.query(
                sqlFriendsByUserId, (rs, rowNum) -> rs.getInt("FRIEND_ID"), id);

        List<User> friendsUser = new ArrayList<>();
        List<User> userList = new ArrayList<>(getUsers());

        for (Integer friend : friends) {
            userList.stream().filter(f -> f.getId() == friend).findFirst().ifPresent(friendsUser::add);
        }
        log.info(friendsUser.toString());
        return friendsUser;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        User userById = getUser(id);
        User otherUserById = getUser(otherId);

        List<User> userFriends = getFriends(userById.getId());
        List<User> otherUserFriends = getFriends(otherUserById.getId());

        List<User> friendList = new ArrayList<>();

        for (User userFriend : userFriends) {
            if (otherUserFriends.contains(userFriend)) {
                friendList.add(userFriend);
            }
        }

        return friendList;
    }

    @SneakyThrows
    static User makeUser(ResultSet rs, int RowNum) {
        return new User(
                rs.getInt("USER_ID"),
                rs.getString("EMAIL"),
                rs.getString("LOGIN"),
                rs.getString("USER_NAME"),
                rs.getDate("BIRTHDAY").toLocalDate()
        );
    }

    @Override
    public void deleteAll() {
        String sqlFilmIds = "DELETE FROM USERS";
        jdbcTemplate.update(sqlFilmIds);
    }
}
