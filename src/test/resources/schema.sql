DROP TABLE IF EXISTS genre_film;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS user_friend;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS ratings;

CREATE TABLE IF NOT EXISTS mpa (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL,
    CONSTRAINT blank_name CHECK(TRIM(name) <> '')
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL,
    description varchar(200),
    release_date date NOT NULL,
    duration INTEGER,
    mpa_id BIGINT REFERENCES mpa(id) ON DELETE RESTRICT,
    CONSTRAINT check_release_date CHECK (release_date >= DATE '1895-12-28'),
    CONSTRAINT min_duration CHECK (duration >= 1),
    CONSTRAINT blank_fields CHECK (TRIM(name) <> ''
    AND TRIM(description) <>'')
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar,
    login varchar NOT NULL,
    name varchar,
    birthday date NOT NULL CHECK (birthday <= CURRENT_DATE),
    CONSTRAINT check_email CHECK (TRIM(email) <> '' AND
    email LIKE '%@%.%'),
    CONSTRAINT check_login CHECK (TRIM(login) <> '')
);

CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT REFERENCES films(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS user_friend (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    friend_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY(user_id, friend_id),
    CONSTRAINT check_relation CHECK(user_id <> friend_id)
);

CREATE TABLE IF NOT EXISTS genres (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL CHECK (TRIM(name) <> '')
);

CREATE TABLE IF NOT EXISTS genre_film (
    genre_id BIGINT REFERENCES genres(id) ON DELETE CASCADE,
    film_id BIGINT REFERENCES films(id) ON DELETE CASCADE,
    PRIMARY KEY(genre_id, film_id)
);