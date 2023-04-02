package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film getFilm(int id);

    Collection<Film> getFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLike(int idFilm, int idUser);

    void removeLike(int idFilm, int idUser);

    public List<Film> getTenMostPopular(Integer count);
}
