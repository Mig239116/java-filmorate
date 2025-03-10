package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;


class FilmorateApplicationTests {

	FilmController filmController;
	UserController userController;

	@BeforeEach
	public void setUp() {
		filmController = new FilmController();
		userController = new UserController();
	}

	@Test
	public void shouldNotAllowAddEmptyNameFilm() {
		Film film = new Film(
				null,
				null,
				"Film",
				LocalDate.now(),
				Duration.ofMinutes(180)
		);
		assertEquals(assertThrows(ValidationException.class, () -> filmController.addFilm(film))
				.getMessage(), "Название не может быть пустым");
	}

	@Test
	public void shouldNotAllowAddBlankNameFilm() {
		Film film = new Film(
				null,
				" ",
				"Film",
				LocalDate.now(),
				Duration.ofMinutes(180)
		);
		assertEquals(assertThrows(ValidationException.class, () -> filmController.addFilm(film))
				.getMessage(), "Название не может быть пустым");
	}

	@Test
	public void shouldNotAllowDescriptionLongerThan200symbols() {
		Film film = new Film(
				null,
				"Film",
				"*".repeat(210),
				LocalDate.now(),
				Duration.ofMinutes(180)
		);
		assertEquals(assertThrows(ValidationException.class, () -> filmController.addFilm(film))
				.getMessage(), "Максимальная длина описания — 200 символов");
	}

	@Test
	public void shouldNotHaveReleaseDateBeforeDefined() {
		Film film = new Film(
				null,
				"Film",
				"Film",
				LocalDate.of(1894, 1, 1),
				Duration.ofMinutes(180)
		);
		assertEquals(assertThrows(ValidationException.class, () -> filmController.addFilm(film))
				.getMessage(), "Дата релиза — не раньше 28 декабря 1895 года");
	}

	@Test
	public void shouldNotHaveDurationZeroOrNegative() {
		Film film = new Film(
				null,
				"Film",
				"Film",
				LocalDate.now(),
				Duration.ofMinutes(-10)
		);
		assertEquals(assertThrows(ValidationException.class, () -> filmController.addFilm(film))
				.getMessage(), "Продолжительность фильма должна быть положительным числом.");
	}

	@Test
	public void shouldNotBlankEmail() {
		User user = new User(
				null,
				"",
				"login",
				"name",
				LocalDate.now()
		);
		assertEquals(assertThrows(ValidationException.class, () -> userController.addUser(user))
				.getMessage(), "Электронная почта не может быть пустой и должна содержать символ @");
	}

	@Test
	public void shouldHaveSpecialSignsInEmail() {
		User user = new User(
				null,
				"email",
				"login",
				"name",
				LocalDate.now()
		);
		assertEquals(assertThrows(ValidationException.class, () -> userController.addUser(user))
				.getMessage(), "Электронная почта не может быть пустой и должна содержать символ @");
	}

	@Test
	public void shouldNotHaveEmptyLogin() {
		User user = new User(
				null,
				"email@email.ru",
				"",
				"name",
				LocalDate.now()
		);
		assertEquals(assertThrows(ValidationException.class, () -> userController.addUser(user))
				.getMessage(), "Логин не может быть пустым и содержать пробелы");
	}

	@Test
	public void shouldNotHaveSpacesInLogin() {
		User user = new User(
				null,
				"email@email.ru",
				"log in",
				"name",
				LocalDate.now()
		);
		assertEquals(assertThrows(ValidationException.class, () -> userController.addUser(user))
				.getMessage(), "Логин не может быть пустым и содержать пробелы");
	}

	@Test
	public void shouldNotHaveBirthdayInFuture() {
		User user = new User(
				null,
				"email@email.ru",
				"login",
				"name",
				LocalDate.now().plusDays(10)
		);
		assertEquals(assertThrows(ValidationException.class, () -> userController.addUser(user))
				.getMessage(), "Дата рождения не может быть в будущем");
	}

	@Test
	public void shouldUseLoginIfNameIsEmpty() {

		User user = new User(
				null,
				"email@email.ru",
				"login",
				"",
				LocalDate.now()
		);
		userController.addUser(user);
		assertEquals(user.getName(), user.getLogin());
	}

	@Test
	public void shouldNotAllowAddBlankDescriptionFilm() {
		Film film = new Film(
				null,
				"Film",
				"",
				LocalDate.now(),
				Duration.ofMinutes(180)
		);
		assertEquals(assertThrows(ValidationException.class, () -> filmController.addFilm(film))
				.getMessage(), "Описание не может быть пустым");
	}

	@Test
	public void shouldNotAllowAddEmptyDescriptionFilm() {
		Film film = new Film(
				null,
				"Film",
				null,
				LocalDate.now(),
				Duration.ofMinutes(180)
		);
		assertEquals(assertThrows(ValidationException.class, () -> filmController.addFilm(film))
				.getMessage(), "Описание не может быть пустым");
	}

	@Test
	public void shouldNotAllowAddEmptyReleaseDateFilm() {
		Film film = new Film(
				null,
				"Film",
				"Film",
				null,
				Duration.ofMinutes(180)
		);
		assertEquals(assertThrows(ValidationException.class, () -> filmController.addFilm(film))
				.getMessage(), "Дата релиза должна быть задана");
	}

	@Test
	public void shouldNotAllowAddEmptyDurationFilm() {
		Film film = new Film(
				null,
				"Film",
				"Film",
				LocalDate.now(),
				null
		);
		assertEquals(assertThrows(ValidationException.class, () -> filmController.addFilm(film))
				.getMessage(), "Длительность должна быть задана");
	}

	@Test
	public void shouldNotHaveEmptyBirthday() {
		User user = new User(
				null,
				"email@email.ru",
				"login",
				"name",
				null
		);
		assertEquals(assertThrows(ValidationException.class, () -> userController.addUser(user))
				.getMessage(), "Дата рождения обязательное поле");
	}

	@Test
	public void shouldNotUpdateIfFilmIdIsNull() {
		Film film = new Film(
				null,
				"Film",
				"Film",
				LocalDate.now(),
				Duration.ofMinutes(180)
		);
		assertEquals(assertThrows(ValidationException.class, () -> filmController.updateFilm(film))
				.getMessage(), "Id должен быть указан");
	}

	@Test
	public void shouldNotUpdateIfFilmIdIsNotFound() {
		Film film = new Film(
				null,
				"Film",
				"Film",
				LocalDate.now(),
				Duration.ofMinutes(180)
		);
		Film filmUpdated = new Film(
				Long.valueOf(3),
				"Film2",
				"Film2",
				LocalDate.now(),
				Duration.ofMinutes(180)
		);
		filmController.addFilm(film);
		assertEquals(assertThrows(NotFoundException.class, () -> filmController.updateFilm(filmUpdated))
				.getMessage(), "Фильм с id = " + filmUpdated.getId() + " не найден!");
	}

	@Test
	public void shouldNotUpdateIfUserIdIsNull() {
		User user = new User(
				null,
				"email@email.ru",
				"login",
				"name",
				LocalDate.now()
		);
		assertEquals(assertThrows(ValidationException.class, () -> userController.updateUser(user))
				.getMessage(), "Id должен быть указан");
	}

	@Test
	public void shouldNotUpdateIfIdIsNotFound() {
		User user = new User(
				null,
				"email@email.ru",
				"login",
				"name",
				LocalDate.now()
		);
		User userUpdated = new User(
				Long.valueOf(3),
				"e@e",
				"login",
				"name",
				LocalDate.now()
		);
		userController.addUser(user);
		assertEquals(assertThrows(NotFoundException.class, () -> userController.updateUser(userUpdated))
				.getMessage(), "Пользователь с id = " + userUpdated.getId() + " не найден!");
	}
}
