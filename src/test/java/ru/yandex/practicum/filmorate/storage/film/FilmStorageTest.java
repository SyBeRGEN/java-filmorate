package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmStorageTest {
    private final FilmStorage filmStorage;


    @Test
    void getFilm() {

        Film film = getFilm("1");
        Film actFilm = filmStorage.createFilm(film);
        actFilm = filmStorage.getFilm(actFilm.getId());
        assertEquals(film.getId(), actFilm.getId());
        assertEquals(film.getName(), actFilm.getName());
    }

    @Test
    void getFilms() {
        filmStorage.deleteAll();
        Film film1 = getFilm("2");
        filmStorage.createFilm(film1);
        Film film2 = getFilm("3");
        filmStorage.createFilm(film2);
        List<Film> expFilms = List.of(film1, film2);

        Collection<Film> actFilms = filmStorage.getFilms();
        List<Film> actFilmsList = new ArrayList<>(actFilms);
        System.out.println(actFilmsList);
        assertEquals(expFilms, actFilmsList);
        assertEquals(2, actFilms.size());
    }

    @Test
    void createFilm() {
        Film film1 = getFilm("4");
        Film actFilm = filmStorage.createFilm(film1);
        actFilm = filmStorage.getFilm(actFilm.getId());
        assertEquals(film1.getName(), actFilm.getName());
        assertEquals(film1.getDescription(), actFilm.getDescription());
        assertEquals(film1.getReleaseDate(), actFilm.getReleaseDate());
        assertEquals(film1.getDuration(), actFilm.getDuration());
        assertEquals(film1.getMpa(), actFilm.getMpa());
        assertEquals(film1.getGenres(), actFilm.getGenres());
    }

    @Test
    void updateFilm() {
        Film film1 = filmStorage.createFilm(getFilm("5"));
        film1.setName("Четкий фильм");
        filmStorage.updateFilm(film1);
        Film actFilm = filmStorage.getFilm(film1.getId());
        assertEquals("Четкий фильм", actFilm.getName());
    }

    @Test
    void getGenreByIdFilm() {
        Film film1 = filmStorage.createFilm(getFilm("6"));
        assertEquals(filmStorage.getFilm(film1.getId()).getGenres(),
                List.of(filmStorage.getGenreById(1), filmStorage.getGenreById(2)));
    }

    @Test
    void getRatingByIdFilm() {
        Film film1 = filmStorage.createFilm(getFilm("7"));
        Film film2 = filmStorage.createFilm(getFilm("8"));
        film2.setMpa(getMPA());
        filmStorage.updateFilm(film2);
        assertEquals(filmStorage.getFilm(film1.getId()).getMpa(), filmStorage.getMpaById(1));
        assertEquals(filmStorage.getFilm(film2.getId()).getMpa(), filmStorage.getMpaById(2));
    }

    private Film getFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("DescOne");
        film.setReleaseDate(LocalDate.of(2023, 5, 10));
        film.setDuration(123);
        MPA rating = new MPA();
        rating.setId(1);
        rating.setName("G");
        film.setMpa(rating);

        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");
        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Драма");
        film.setGenres(List.of(genre1, genre2));
        return film;
    }

    private MPA getMPA() {
        MPA gating = new MPA();
        gating.setId(2);
        gating.setName("PG");
        return gating;
    }

}