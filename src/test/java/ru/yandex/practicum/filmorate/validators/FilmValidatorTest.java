package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationExcpetions;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.FilmValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidatorTest {
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static Film film;

    @BeforeEach
    public void init() {
        film = new Film();
        film.setName("Name");
        String description = "d".repeat(MAX_DESCRIPTION_LENGTH);
        film.setDescription(description);
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
    }

    @Test
    public void shouldValidateReleaseDateEqualsCinemaBirthday() {
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film),
                "Выбрасывается исключение при дате релиза равном дню рождения кино");
    }

    @Test
    public void shouldNotValidateReleaseDateEarlyThanCinemaBirthday() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationExcpetions.class, () -> FilmValidator.validateFilm(film),
                "Не выбрасывается исключение при дате релиза раньше, чем день рождения кино");
    }
}
