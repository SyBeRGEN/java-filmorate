package ru.yandex.practicum.filmorate.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.SearchException;
import ru.yandex.practicum.filmorate.exceptions.ValidationExcpetions;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Slf4j
public class FilmValidator {
    @SneakyThrows
    public static void validateFilm(Film film) {
        if (film.getName().isBlank()) {
            log.error("Название не может быть пустым: {}", film);
            throw new ValidationExcpetions("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Длина описания не должна быть больше 200 символов: {}", film);
            throw new ValidationExcpetions("Длина описания не должна быть больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза — не должна быть раньше 28 декабря 1895 года: {}", film);
            throw new ValidationExcpetions("Дата релиза — не должна быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность отрицательная: {}", film);
            throw new ValidationExcpetions("Продолжительность отрицательная");
        }
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            log.error("Рейтинг MPAA не может быть пустым: {}", film);
            throw new ValidationExcpetions("Рейтинг MPAA не может быть пустым");
        }
    }

    @SneakyThrows
    public static void validateCreation(List<Integer> filmIdList, Film film) {
        if (film.getId() != null) {
            if (filmIdList.contains(film.getId())) {
                log.error("Фильм уже был создан: {}", film);
                throw new ValidationExcpetions("Фильм уже был создан");
            }
        }
    }

    @SneakyThrows
    public static void validateUpdate(List<Integer> filmIdList, Film film) {
        if (film.getId() == null) {
            log.error("Фильм еще не был создан: {}", film);
            throw new ValidationExcpetions("Фильм еще не был создан");
        }
        if (!filmIdList.contains(film.getId())) {
            log.error("Фильм еще не был создан: {}", film);
            throw new SearchException("Фильм еще не был создан");
        }
    }

    @SneakyThrows
    public static void validateExist(List<Integer> filmIdList, Integer id) {
        if (!filmIdList.contains(id)) {
            throw new SearchException(String.format("Фильм с id %s не найден", id));
        }
    }

    @SneakyThrows
    public static void validateMPA(List<Integer> mpaIdList, Integer id) {
        if (!mpaIdList.contains(id)) {
            throw new ValidationExcpetions(String.format("MPA c id %s не найден", id));
        }
    }

    @SneakyThrows
    public static void validateExistMPA(List<MPA> mpaIdList, Integer id) {
        if (mpaIdList.isEmpty() || mpaIdList.get(0) == null || !mpaIdList.get(0).getId().equals(id)) {
            throw new SearchException(String.format("MPA c id %s не найден", id));
        }
    }

    @SneakyThrows
    public static void validateGenre(List<Integer> genreList, Integer id) {
        if (!genreList.contains(id)) {
            throw new ValidationExcpetions(String.format("Жанр c id %s не найден", id));
        }
    }

    @SneakyThrows
    public static void validateExistGenre(List<Genre> genreList, Integer id) {
        if (genreList.isEmpty() || genreList.get(0) == null || !(genreList.get(0).getId() == id)) {
            throw new SearchException(String.format("Жанр c id %s не найден", id));
        }
    }

    @SneakyThrows
    public static void validateLike(List<Integer> userLikes, Integer id) {
        if (userLikes.contains(id)) {
            log.error("Фильм уже был лайкнут пользователем: {}", userLikes);
            throw new ValidationExcpetions("Фильм уже был лайкнут пользователем");
        }

    }
}
