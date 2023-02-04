package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationExcpetions;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private static int userId = 1;
    private final HashMap<Integer, User> userList = new HashMap<>();
    @GetMapping()
    public Collection<User> getUsers() {
        log.info("Вызван метод getUsers");
        return userList.values();
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) throws ValidationExcpetions {
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
            return user;
        }
    }

    @PutMapping()
    public User putUser(@Valid @RequestBody User user) throws ValidationExcpetions {
        log.info("Вызван метод putUser");
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationExcpetions("Логин не может быть пустым или содержать пробелы");
        }  else if (!userList.containsKey(user.getId())) {
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
}
