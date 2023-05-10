package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidatorTest {
    public static User user;

    @BeforeEach
    public void init() {
        user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login");
        user.setBirthday(LocalDate.now());
    }

    @Test
    public void shouldUseNonEmptyName() {
        String name = "name";
        user.setName(name);
        UserValidator.validateUser(user);
        String actualName = user.getName();
        assertEquals(actualName, name, "Изменяется имя, если оно не пустое");
    }

    @Test
    public void shouldUseLoginWhenNameIsEmpty() {
        user.setName("");
        UserValidator.validateUser(user);
        String actualName = user.getName();
        assertEquals(actualName, user.getLogin(), "Не используется login при пустом имени");
    }

    @Test
    public void shouldUseLoginWhenNameIsNull() {
        user.setName(null);
        UserValidator.validateUser(user);
        String actualName = user.getName();
        assertEquals(actualName, user.getLogin(), "Не используется login при имени = null");
    }
}
