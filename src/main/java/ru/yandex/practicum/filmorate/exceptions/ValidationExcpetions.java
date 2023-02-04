package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationExcpetions extends Exception {
    public ValidationExcpetions(String message) {
        super(message);
        log.warn(message);
    }
}
