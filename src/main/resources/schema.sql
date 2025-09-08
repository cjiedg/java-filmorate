CREATE TABLE IF NOT EXISTS rating (
    rating_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    duration INTEGER NOT NULL,
    release_date DATE NOT NULL,
    rating_id INTEGER REFERENCES rating(rating_id)
);

CREATE TABLE IF NOT EXISTS genre_film (
    genre_id INTEGER REFERENCES genres(genre_id),
    film_id INTEGER REFERENCES films(film_id),
    PRIMARY KEY (genre_id, film_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id INTEGER REFERENCES users(user_id),
    friend_id INTEGER REFERENCES users(user_id),
    status BOOLEAN NOT NULL,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_likes (
    user_id INTEGER REFERENCES users(user_id),
    film_id INTEGER REFERENCES films(film_id),
    PRIMARY KEY (user_id, film_id)
);