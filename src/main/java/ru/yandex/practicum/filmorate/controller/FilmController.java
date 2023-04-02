package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;

    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        return filmStorage.getTenMostPopular(count);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLikes(@PathVariable int id, @PathVariable int userId) {
        filmStorage.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmStorage.removeLike(id, userId);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmStorage.getFilm(id);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @PostMapping()
    public Film postFilm(@RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    @PutMapping()
    public Film putFilm(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

}
