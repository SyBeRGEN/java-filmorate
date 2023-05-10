package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.Storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("/schema.sql")
public class UserDbStorageTest extends StorageTest<User> {
    @Autowired
    public UserDbStorageTest(@Qualifier("UserDBStorage") UserStorage userStorage) {
        super((Storage<User>) userStorage);
    }

    @BeforeEach
    @Override
    void initData() {
        data1 = User.builder()
                .name("Иван")
                .login("ivan2000")
                .email("ivanov_i@yandex.ru")
                .birthday(LocalDate.of(2000, 8, 8))
                .build();

        data2 = User.builder()
                .name("Степан")
                .login("im_stepan")
                .email("st_petrov@yandex.ru")
                .birthday(LocalDate.of(1995, 11, 12))
                .build();

        data3 = User.builder()
                .name("Екатерина")
                .login("katya2028")
                .email("20kat28@yandex.ru")
                .birthday(LocalDate.of(2005, 5, 23))
                .build();
    }

    @Test
    void shouldReturnUserListById() {
        data1 = storage.add(data1);
        data2 = storage.add(data2);
        data3 = storage.add(data3);
        List<User> expected = List.of(data1, data2, data3);

        UserStorage userStorage = (UserStorage) storage;
        Collection<User> actual = userStorage.getUsers();

        assertThat(actual).isNotNull()
                .hasSize(expected.size())
                .isSubsetOf(expected);
    }
}
