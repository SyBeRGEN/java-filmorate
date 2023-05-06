package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    List<Genre> getGenreList();

    Genre getGenreById(Integer id);

    List<MPA> getMpaList();

    MPA getMpaById(Integer id);

    Film getFilm(int id);

    Collection<Film> getFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLike(int idFilm, int idUser);

    void removeLike(int idFilm, int idUser);

    public List<Film> getTenMostPopular(Integer count);

}
