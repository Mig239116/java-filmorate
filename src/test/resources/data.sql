INSERT INTO mpa (name) SELECT 'G'
WHERE NOT EXISTS (SELECT name FROM mpa WHERE name = 'G');

INSERT INTO mpa (name) SELECT 'PG'
WHERE NOT EXISTS (SELECT name FROM mpa WHERE name = 'PG');

INSERT INTO mpa (name) SELECT 'PG-13'
WHERE NOT EXISTS (SELECT name FROM mpa WHERE name = 'PG-13');

INSERT INTO mpa (name) SELECT 'R'
WHERE NOT EXISTS (SELECT name FROM mpa WHERE name = 'R');

INSERT INTO mpa (name) SELECT 'NC-17'
WHERE NOT EXISTS (SELECT name FROM mpa WHERE name = 'NC-17');

INSERT INTO genres (name) SELECT 'Комедия'
WHERE NOT EXISTS (SELECT name FROM genres WHERE name = 'Комедия');

INSERT INTO genres (name) SELECT 'Драма'
WHERE NOT EXISTS (SELECT name FROM genres WHERE name = 'Драма');

INSERT INTO genres (name) SELECT 'Мультфильм'
WHERE NOT EXISTS (SELECT name FROM genres WHERE name = 'Мультфильм');

INSERT INTO genres (name) SELECT 'Триллер'
WHERE NOT EXISTS (SELECT name FROM genres WHERE name = 'Триллер');

INSERT INTO genres (name) SELECT 'Документальный'
WHERE NOT EXISTS (SELECT name FROM genres WHERE name = 'Документальный');

INSERT INTO genres (name) SELECT 'Боевик'
WHERE NOT EXISTS (SELECT name FROM genres WHERE name = 'Боевик');

INSERT INTO users (email, login, name, birthday) VALUES ('mail1@mail.ru', 'login1', 'name1', '2005-02-02');
INSERT INTO users (email, login, name, birthday) VALUES ('mail2@mail.ru', 'login2', 'name2', '2005-02-02');
INSERT INTO users (email, login, name, birthday) VALUES ('mail3@mail.ru', 'login3', 'name3', '2005-02-02');
INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES ('film1', 'film1', '1980-01-01', 100, 1);
INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES ('film2', 'film2', '1980-01-01', 200, 2);
INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES ('film3', 'film3', '1980-01-01', 300, 3);