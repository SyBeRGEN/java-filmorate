package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

public interface Storage<T extends Edata> extends FilmStorage {
    T add(T data);

    T update(T data);

    List<T> findAll();

    T findById(Long id);

    default boolean contains(Long id) {
        return findById(id) != null;
    }
}
