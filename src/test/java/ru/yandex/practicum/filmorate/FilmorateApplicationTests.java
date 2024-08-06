package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class,
        FilmDbStorage.class, FilmRowMapper.class, GenreDbService.class, GenreRowMapper.class,
        MpaDbService.class, MpaRowMapper.class, FieldsValidatorService.class, FilmFieldsDbValidatorService.class,
UserFieldsDbValidatorService.class})
class FilmorateApplicationTests {

    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenreDbService genreDbService;
    private final MpaDbService mpaDbService;

    @Autowired
    public FilmorateApplicationTests(UserDbStorage userDbStorage, FilmDbStorage filmDbStorage,
                                     GenreDbService genreDbService, MpaDbService mpaDbService) {
        this.userDbStorage = userDbStorage;
        this.filmDbStorage = filmDbStorage;
        this.genreDbService = genreDbService;
        this.mpaDbService = mpaDbService;
    }

     public void addTestFilm() {
        Set<Genre> genres = new HashSet<>();
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);
        Film film = new Film();
        film.setName("Джентльмены");
        film.setGenres(genres);
        film.setMpa(mpa);
        film.setReleaseDate(LocalDate.of(2019, 1, 1));
        film.setDuration(150L);
        filmDbStorage.addFilm(film);
    }

    public void addTestUser() {
        User user = new User("email@email.ru", "login", "testName", LocalDate.now());
        userDbStorage.createUser(user);
    }


    @BeforeEach
    void setUp() {
        addTestFilm();
        addTestUser();
    }

    @Test
    @DirtiesContext
    void getFilmById() {
        Film film = filmDbStorage.getFilmById(1L);
        assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DirtiesContext
    void addLikeTest() {
        User user = userDbStorage.getUserById(1L);
        filmDbStorage.addLike(1L, 1L);
        Film film = filmDbStorage.getFilmById(1L);
        assertTrue(film.getLikes().contains(user.getId()));
    }

    @Test
    @DirtiesContext
    void deleteLikeTest() {
        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.deleteLike(1L, 1L);
        Film film = filmDbStorage.getFilmById(1L);
        assertTrue(film.getLikes().isEmpty());
    }

    @Test
    @DirtiesContext
    void getPopularTest() {
        filmDbStorage.addLike(1L, 1L);
        List<Film> popular = filmDbStorage.getMostLiked(1);
        assertEquals(1, popular.size());
    }

    @Test
    @DirtiesContext
    void getAllFilmsTest() {
        List<Film> films = filmDbStorage.getAll().stream().toList();
        assertEquals(1, films.size());
    }

    @Test
    @DirtiesContext
    void updateFilmTest() {
        Set<Genre> genres = new HashSet<>();
        Mpa mpa = new Mpa();
        mpa.setId(2);
        mpa.setName("PG");
        Genre genre = new Genre();
        genre.setId(2);
        genre.setName("Драма");
        genres.add(genre);
        Film updatedFilm = new Film();
        updatedFilm.setName("Джентльмены2");
        updatedFilm.setGenres(genres);
        updatedFilm.setMpa(mpa);
        updatedFilm.setReleaseDate(LocalDate.of(2018, 1, 1));
        updatedFilm.setDuration(155L);
        updatedFilm.setId(1L);
        Film film = filmDbStorage.getFilmById(1L);
        assertEquals("Джентльмены", film.getName());
        filmDbStorage.update(updatedFilm);
        film = filmDbStorage.getFilmById(1L);
        assertEquals("Джентльмены2", film.getName());
    }

    @Test
    @DirtiesContext
    void findUserByIdTest() {
        User user = userDbStorage.getUserById(1L);
        assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @DirtiesContext
    void getAllUsersTest() {
        List<User> users = userDbStorage.getAll().stream().toList();
        assertEquals(1, users.size());
    }

    @Test
    @DirtiesContext
    void createUserTest() {
        User user = new User("newemail@email.ru", "login2", "testName", LocalDate.now());
        userDbStorage.createUser(user);
        List<User> users = userDbStorage.getAll().stream().toList();
        assertEquals(2, users.size());
    }

    @Test
    @DirtiesContext
    void updateUserTest() {
        User user = userDbStorage.getUserById(1L);
        user.setEmail("newemail@email.ru");
        userDbStorage.update(user);
        User updatedUser = userDbStorage.getUserById(1L);
        assertEquals("newemail@email.ru", updatedUser.getEmail());
    }

    @Test
    @DirtiesContext
    void addFriendTest() {
        User user = userDbStorage.getUserById(1L);
        User friend = new User("newemail@email.ru", "login2", "testName", LocalDate.now());
        userDbStorage.createUser(friend);
        userDbStorage.addFriend(user.getId(), 2L);
        user = userDbStorage.getUserById(1L);
        assertTrue(user.getFriends().contains(friend.getId()));
    }

    @Test
    @DirtiesContext
    void deleteFriendTest() {
        User user = userDbStorage.getUserById(1L);
        User friend = new User("newemail@email.ru", "login2", "testName", LocalDate.now());
        userDbStorage.createUser(friend);
        userDbStorage.addFriend(user.getId(), 2L);
        userDbStorage.deleteFriend(user.getId(), 2L);
        user = userDbStorage.getUserById(1L);
        assertFalse(user.getFriends().contains(friend.getId()));
    }


    @Test
    @DirtiesContext
    void getFriendsTest() {
        User user = userDbStorage.getUserById(1L);
        User friend = new User("newemail@email.ru", "login2", "testName", LocalDate.now());
        userDbStorage.createUser(friend);
        userDbStorage.addFriend(user.getId(), 2L);
        List<User> friends = userDbStorage.getUserFriends(user.getId());
        assertEquals(1, friends.size());
    }

    @Test
    @DirtiesContext
    void getCommonFriendsTest() {
        User user = userDbStorage.getUserById(1L);
        User user2 = new User("newemail@email.ru", "login2", "testName", LocalDate.now());
        User user3 = new User("neweremail@email.ru", "login3", "testName", LocalDate.now());
        userDbStorage.createUser(user2);
        userDbStorage.createUser(user3);
        userDbStorage.addFriend(user.getId(), 2L);
        userDbStorage.addFriend(user.getId(), 3L);
        userDbStorage.addFriend(2L, 3L);
        userDbStorage.addFriend(3L, 2L);
        userDbStorage.addFriend(2L, 1L);
        List<User> commonFriends = userDbStorage.getCommonFriends(user.getId(), 2L);
        assertEquals(1, commonFriends.size());
    }

    @Test
    @DirtiesContext
    void getAllGenresTest() {
        List<Genre> genres = genreDbService.findAll().stream().toList();
        assertEquals(6, genres.size());
    }

    @Test
    @DirtiesContext
    void getGenreByIdTest() {
        Genre genre = genreDbService.findById(1);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @DirtiesContext
    void getAllMpaTest() {
        List<Mpa> mpa = mpaDbService.findAll().stream().toList();
        assertEquals(5, mpa.size());
    }

    @Test
    @DirtiesContext
    void getMpaByIdTest() {
        Mpa mpa = mpaDbService.findById(1);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }
}
