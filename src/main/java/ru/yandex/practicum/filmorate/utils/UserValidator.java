package ru.yandex.practicum.filmorate.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exceptions.SearchException;
import ru.yandex.practicum.filmorate.exceptions.ValidationExcpetions;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class UserValidator {
    @SneakyThrows
    public static void validateUser(User user) {
        if (!StringUtils.hasText(user.getEmail()) || !user.getEmail().contains("@")) {
            log.error("Email не может быть пустым и должен содержать @: {}", user);
            throw new ValidationExcpetions("Email не может быть пустым и должен содержать @");
        }
        if (!StringUtils.hasText(user.getLogin()) || StringUtils.containsWhitespace(user.getLogin())) {
            log.error("Логин не может быть пустым или содержать пробелы: {}", user);
            throw new ValidationExcpetions("Логин не может быть пустым или содержать пробелы");
        }
        if (!StringUtils.hasText(user.getName())) {
            log.warn("Пользователь ввел пустое имя: {}", user);
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Введен не правильный день рождения: {}", user);
            throw new ValidationExcpetions("Введен не правильный день рождения");
        }
    }

    @SneakyThrows
    public static void validateCreation(List<Integer> userIdList, User user) {
        if (userIdList.contains(user.getId())) {
            log.warn("Пользователь уже создан");
            throw new ValidationExcpetions("Пользователь уже создан");
        }
    }

    @SneakyThrows
    public static void validateUpdate(List<Integer> userIdList, User user) {
        if (!userIdList.contains(user.getId())) {
            log.error("Нет такого Id: {}", user);
            throw new SearchException("Нет такого Id");
        }
    }

    @SneakyThrows
    public static void validateExist(List<Integer> userIdList, Integer id) {
        if (!userIdList.contains(id)) {
            throw new SearchException(String.format("Пользователь с указанным id %s не найден", id));
        }
    }

}
