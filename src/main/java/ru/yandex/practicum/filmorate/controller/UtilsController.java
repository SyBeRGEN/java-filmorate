package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Slf4j
@RestController
public class UtilsController {
    private final FilmStorage filmStorage;

    public UtilsController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping("/genres")
    public List<Genre> findAllGenres() {
        List<Genre> genres = filmStorage.getGenreList();
        log.info("Жанры: {}", genres);
        return genres;
    }

    @GetMapping("/genres/{id}")
    public Genre findGenreById(@PathVariable(name = "id") Integer id) {
        Genre genre = filmStorage.getGenreById(id);
        log.info("Жанр по id{} : {}", id, genre);
        return genre;
    }

    @GetMapping("/mpa")
    public List<MPA> findAllRatings() {
        List<MPA> ratings = filmStorage.getMpaList();
        log.info("MPAA: {}", ratings);
        return ratings;
    }

    @GetMapping("/mpa/{id}")
    public MPA findRatingById(@PathVariable int id) {
        MPA mpa = filmStorage.getMpaById(id);
        log.info("MPAA по id {} : {}", id, mpa);
        return mpa;
    }
}
