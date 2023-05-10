package ru.yandex.practicum.filmorate.storage.film;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.utils.FilmValidator;
import ru.yandex.practicum.filmorate.utils.UserValidator;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
@Qualifier("FilmDBStorage")
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenreList() {
        String sqlGenres = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlGenres, FilmDBStorage::makeGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        String sqlGenres = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        List<Genre> genresById = jdbcTemplate.query(sqlGenres, FilmDBStorage::makeGenre, id);
        FilmValidator.validateExistGenre(genresById, id);
        return genresById.get(0);
    }

    @Override
    public List<MPA> getMpaList() {
        String sqlMpa = "SELECT * FROM RATINGS";
        return jdbcTemplate.query(sqlMpa, FilmDBStorage::makeMpa);
    }

    @Override
    public MPA getMpaById(Integer id) {
        String sqlMpa = "SELECT * FROM RATINGS WHERE RATINGMPAA_ID = ?";
        List<MPA> mpaById = jdbcTemplate.query(sqlMpa, FilmDBStorage::makeMpa, id);
        FilmValidator.validateExistMPA(mpaById, id);
        return mpaById.get(0);
    }

    @Override
    public Film getFilm(int id) {
        String sqlFilmIdList = "SELECT FILM_ID FROM FILMS";
        List<Integer> filmIdList = jdbcTemplate.query(
                sqlFilmIdList, (rs, rowNum) -> rs.getInt("FILM_ID"));
        FilmValidator.validateExist(filmIdList, id);

        String sqlFilmById = "SELECT * " +
                "FROM FILMS LEFT OUTER JOIN RATINGS " +
                "ON FILMS.RATINGMPAA_ID = RATINGS.RATINGMPAA_ID " +
                "WHERE FILM_ID = ?";
        List<Film> films = jdbcTemplate.query(sqlFilmById, FilmDBStorage::makeFilm, id);
        Film film = films.get(0);

        String sqlFilmGenres = "SELECT * " +
                "FROM FILM_GENRES LEFT OUTER JOIN GENRES " +
                "ON GENRES.GENRE_ID = FILM_GENRES.GENRE_ID " +
                "WHERE FILM_ID = ?";
        List<Genre> filmGenres = jdbcTemplate.query(
                sqlFilmGenres, FilmDBStorage::makeGenre, film.getId());

        film.setGenres(filmGenres);
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT *" +
                "FROM FILMS " +
                "LEFT OUTER JOIN RATINGS ON FILMS.RATINGMPAA_ID = RATINGS.RATINGMPAA_ID " +
                "LEFT OUTER JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID " +
                "LEFT OUTER JOIN GENRES ON GENRES.GENRE_ID = FILM_GENRES.GENRE_ID";

        Map<Integer, Film> filmsMap = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            int id = rs.getInt("FILM_ID");
            Film film = filmsMap.get(id);
            if (film == null) {
                film = new Film(
                        rs.getString("FILM_NAME"),
                        rs.getString("DESCRIPTION"),
                        rs.getDate("RELEASE_DATE").toLocalDate(),
                        rs.getInt("DURATION"),
                        new MPA(rs.getInt("RATINGMPAA_ID"), rs.getString("RATING_NAME")
                        ));
                film.setId(rs.getInt("FILM_ID"));
                filmsMap.put(id, film);
            }

            int genreId = rs.getInt("GENRE_ID");
            if (!rs.wasNull()) {
                String genreName = rs.getString("GENRE_NAME");
                Genre genre = new Genre(genreId, genreName);
                film.getGenres().add(genre);
            }
        });

        return filmsMap.values();
    }

    @Override
    public Film createFilm(Film film) {
        FilmValidator.validateFilm(film);

        //film
        String sqlFilmIdList = "SELECT FILM_ID FROM FILMS";
        List<Integer> filmIdList = jdbcTemplate.query(
                sqlFilmIdList, (rs, rowNum) -> rs.getInt("FILM_ID"));
        FilmValidator.validateCreation(filmIdList, film);

        // mpa
        Integer mpaId = film.getMpa().getId();
        String sqlMpa = "SELECT * FROM RATINGS WHERE RATINGMPAA_ID = ?";
        List<MPA> mpaById = jdbcTemplate.query(sqlMpa, FilmDBStorage::makeMpa, mpaId);
        FilmValidator.validateExistMPA(mpaById, mpaId);
        MPA mpa = mpaById.get(0);

        //genre
        Set<Integer> genreSet = film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        String genreIds = genreSet.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sqlGenres = "SELECT * FROM GENRES WHERE GENRE_ID IN (" + genreIds + ")";
        List<Genre> filmGenres = jdbcTemplate.query(sqlGenres, FilmDBStorage::makeGenre);
        for (Integer integer : genreSet) {
            FilmValidator.validateExistGenre(filmGenres, integer);
        }

        //film --> model
        film.setGenres(filmGenres);
        film.setMpa(mpa);

        //film --> DB
        String sqlCreateFilm = "INSERT INTO FILMS(" +
                "FILM_NAME, " +
                "DESCRIPTION, " +
                "RELEASE_DATE, " +
                "DURATION, " +
                "RATINGMPAA_ID) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlCreateFilm, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        Integer filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(filmId);

        //film_genres --> DB
        String sqlCreateFilmGenre = "INSERT INTO FILM_GENRES(" +
                "FILM_ID, " +
                "GENRE_ID) " +
                "VALUES (?, ?)";
        for (Integer genreId : genreSet) {
            jdbcTemplate.update(sqlCreateFilmGenre, film.getId(), genreId);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        FilmValidator.validateFilm(film);

        //film_update validation
        String sqlFilmIdList = "SELECT FILM_ID FROM FILMS";
        List<Integer> filmIdList = jdbcTemplate.query(
                sqlFilmIdList, (rs, rowNum) -> rs.getInt("FILM_ID"));
        FilmValidator.validateUpdate(filmIdList, film);

        //mpa
        Integer mpaId = film.getMpa().getId();
        String sqlMpa = "SELECT * FROM RATINGS WHERE RATINGMPAA_ID = ?";
        List<MPA> mpaById = jdbcTemplate.query(sqlMpa, FilmDBStorage::makeMpa, mpaId);
        FilmValidator.validateExistMPA(mpaById, mpaId);
        MPA mpa = mpaById.get(0);

        //genre
        Set<Integer> genreSet = film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        String genreIds = genreSet.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sqlGenres = "SELECT * FROM GENRES WHERE GENRE_ID IN (" + genreIds + ")";
        List<Genre> filmGenres = jdbcTemplate.query(sqlGenres, FilmDBStorage::makeGenre);

        //film --> model
        film.setGenres(filmGenres);
        film.setMpa(mpa);

        //film --> DB
        String sqlUpdateFilm = "UPDATE FILMS SET " +
                "FILM_NAME = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION = ?, " +
                "RATINGMPAA_ID = ? " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlUpdateFilm, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        //remove film_genre --> DB
        String sqlDeleteFilmGenre = "DELETE FROM FILM_GENRES " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlDeleteFilmGenre, film.getId());

        //put film_genre --> DB
        String sqlCreateFilmGenre = "INSERT INTO FILM_GENRES(" +
                "FILM_ID, " +
                "GENRE_ID) " +
                "VALUES (?, ?)";
        for (Integer genreId : genreSet) {
            jdbcTemplate.update(sqlCreateFilmGenre, film.getId(), genreId);
        }
        return film;
    }

    @Override
    public void addLike(int idFilm, int idUser) {
        String sqlFilmLikesById = "SELECT * FROM LIKES WHERE FILM_ID = ?";
        List<Integer> filmLikesList = jdbcTemplate.query(sqlFilmLikesById, (rs, rowNum) -> rs.getInt("USER_ID"), idFilm);
        FilmValidator.validateLike(filmLikesList, idUser);

        String sqlCreateLike =
                "INSERT INTO LIKES(" +
                        "FILM_ID, " +
                        "USER_ID) " +
                        "VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlCreateLike, new String[]{"LIKE_ID"});
            stmt.setLong(1, idFilm);
            stmt.setLong(2, idUser);
            return stmt;
        }, keyHolder);

    }

    @Override
    public void removeLike(int idFilm, int idUser) {
        String sqlFilmLikesById = "SELECT * FROM LIKES WHERE FILM_ID = ?";
        List<Integer> filmLikesList = jdbcTemplate.query(sqlFilmLikesById, (rs, rowNum) -> rs.getInt("USER_ID"), idFilm);
        UserValidator.validateExist(filmLikesList, idUser);

        String sqlDeleteLike = "DELETE FROM LIKES " +
                "WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlDeleteLike, idFilm, idUser);
    }

    @Override
    public List<Film> getTenMostPopular(Integer count) {
        if (count == null) {
            count = 10;
        }
        //get film id's
        String sqlFilmIds = "SELECT FILM_ID FROM FILMS";
        List<Integer> filmIdsList = jdbcTemplate.queryForList(sqlFilmIds, Integer.class);
        //collector
        HashMap<Integer, Integer> mostPopularMap = (HashMap<Integer, Integer>) filmIdsList.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        filmId -> jdbcTemplate.queryForList("SELECT USER_ID FROM LIKES WHERE FILM_ID = ?", Integer.class, filmId).size()
                ));
        List<Integer> mostTenSorted = byLikesDesc(mostPopularMap);
        List<Film> mostTenSortedFilm = new ArrayList<>();
        //return 10 films
        for (Integer id : mostTenSorted) {
            mostTenSortedFilm.add(getFilm(id));
        }
        return mostTenSortedFilm.stream().limit(count).collect(Collectors.toList());
    }

    public static List<Integer> byLikesDesc(Map<Integer, Integer> idLikeCountMap) {
        return idLikeCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    static MPA makeMpa(ResultSet rs, int rowNum) {
        //Создание mpa
        return new MPA(
                rs.getInt("RATINGMPAA_ID"),
                rs.getString("RATING_NAME")
        );
    }

    @SneakyThrows
    static Genre makeGenre(ResultSet rs, int rowNum) {
        return new Genre(
                rs.getInt("GENRE_ID"),
                rs.getString("GENRE_NAME")
        );
    }

    @SneakyThrows
    static Film makeFilm(ResultSet rs, int rowNum) {
        //create film
        Film film = new Film(
                rs.getString("FILM_NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getInt("DURATION"),
                new MPA(rs.getInt("RATINGMPAA_ID"), rs.getString("RATING_NAME"))
        );
        film.setId(rs.getInt("FILM_ID"));
        return film;
    }

    @Override
    public void deleteAll() {
        String sqlFilmIds = "DELETE FROM FILMS";
        jdbcTemplate.update(sqlFilmIds);
    }
}
