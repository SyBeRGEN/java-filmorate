package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmExceptions;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    protected static int id = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();
    @GetMapping()
    public java.util.Collection<Film> getFilms() {
        log.info("Вызван метод getFilms");
        return films.values();
    }
    @PostMapping()
    public Film postFilm(@RequestBody Film film) throws FilmExceptions {
        log.info("Вызван метод postFilm");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmExceptions("Дата релиза — не должна быть раньше 28 декабря 1895 года");
        }  else if (film.getName().isEmpty()) {
            throw new FilmExceptions("Название не может быть пустым");
        }  else if (film.getDescription().length() > 200) {
            throw new FilmExceptions("Длина описания не должна быть больше 200 символов");
        } else if (film.getDuration() < 0) {
            throw new FilmExceptions("Продолжительность отрицательная");
        } else {
            film.setId(id);
            id++;
            films.put(film.getId(), film);
            return film;
        }
    }

    @PutMapping()
    public Film putFilm(@RequestBody Film film) throws FilmExceptions {
        log.info("Вызван метод putFilm");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmExceptions("Дата релиза — не должна быть раньше 28 декабря 1895 года");
        } else if (!films.containsKey(film.getId())) {
            throw new FilmExceptions("Фильма с таким id не существует");
        } else if (film.getName().isEmpty()) {
            throw new FilmExceptions("Название не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new FilmExceptions("Длина описания не должна быть больше 200 символов");
        } else if (film.getDuration() < 0) {
            throw new FilmExceptions("Продолжительность отрицательная");
        } else {
            films.put(film.getId(), film);
            return film;
        }
    }

}
