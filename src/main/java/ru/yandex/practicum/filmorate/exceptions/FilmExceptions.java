package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmExceptions extends Exception {
    public FilmExceptions(String message) {
        super(message);
        log.warn(message);
    }
}
