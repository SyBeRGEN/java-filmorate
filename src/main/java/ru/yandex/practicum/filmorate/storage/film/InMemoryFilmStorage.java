package ru.yandex.practicum.filmorate.storage.film;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmExceptions;
import ru.yandex.practicum.filmorate.exceptions.SearchException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film getFilm(int id) {
        if (films.containsKey(id)) {
            log.info("Вызван метод getFilm");
            return films.get(id);
        }
        throw new SearchException("Данный Id фильма не найден");
    }

    @Override
    public Collection<Film> getFilms() {
        log.info("Вызван метод getFilms");
        return films.values();
    }

    @SneakyThrows
    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmExceptions("Дата релиза — не должна быть раньше 28 декабря 1895 года");
        } else if (film.getName().isEmpty()) {
            throw new FilmExceptions("Название не может быть пустым");
        } else if (film.getDescription().length() > 200) {
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

    @SneakyThrows
    @Override
    public Film updateFilm(Film film) {
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

    @Override
    public void addLike(int idFilm, int idUser) {
        if (films.containsKey(idFilm)) {
            films.get(idFilm).likes.put(idUser, true);
            films.get(idFilm).likesCounter();
        } else throw new SearchException("Данный Id фильма не найден");
    }

    @Override
    public void removeLike(int idFilm, int idUser) {
        if (films.containsKey(idFilm)) {
            if (films.get(idFilm).likes.containsKey(idUser)) {
                films.get(idFilm).likes.put(idUser, false);
                films.get(idFilm).likesCounter();
            } else throw new SearchException("Данный пользовательно не воздействовал на лайки");
        } else throw new SearchException("Данный Id фильма не найден");
    }

    @Override
    public List<Film> getTenMostPopular(Integer count) {
        if (count == null) {
            count = 10;
        }

        return byFilm(films, Comparator.<Film>comparingInt(Film::getLikesCount).reversed()).values().stream().limit(count).collect(Collectors.toList());
    }

    public static HashMap<Integer, Film> byFilm(
            Map<Integer, Film> films, Comparator<Film> valueComparator
    ) {
        return films.entrySet()
                .stream() // Stream<Map.Entry<Integer, Film>>
                .sorted(HashMap.Entry.comparingByValue(valueComparator))
                .collect(Collectors.toMap(
                        HashMap.Entry::getKey,
                        HashMap.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
