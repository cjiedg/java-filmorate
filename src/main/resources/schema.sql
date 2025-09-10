
DROP TABLE IF EXISTS film_likes;
DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS genre_film;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS rating;


CREATE TABLE rating (
    rating_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);


CREATE TABLE genres (
    genre_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);


CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    birthday DATE NOT NULL
);


CREATE TABLE films (
    film_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    duration BIGINT NOT NULL,
    release_date DATE NOT NULL,
    rating_id INT,
    CONSTRAINT fk_rating FOREIGN KEY (rating_id) REFERENCES rating(rating_id)
);


CREATE TABLE genre_film (
    genre_id INT,
    film_id INT,
    PRIMARY KEY (genre_id, film_id),
    CONSTRAINT fk_genre FOREIGN KEY (genre_id) REFERENCES genres(genre_id),
    CONSTRAINT fk_film FOREIGN KEY (film_id) REFERENCES films(film_id)
);


CREATE TABLE friendship (
    user_id INT,
    friend_id INT,
    status BOOLEAN NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_user_friendship FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_friend_friendship FOREIGN KEY (friend_id) REFERENCES users(user_id)
);


CREATE TABLE film_likes (
    user_id INT,
    film_id INT,
    PRIMARY KEY (user_id, film_id),
    CONSTRAINT fk_user_like FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_film_like FOREIGN KEY (film_id) REFERENCES films(film_id)
);


INSERT INTO rating (name) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');


INSERT INTO genres (name) VALUES 
('Комедия'), 
('Драма'), 
('Мультфильм'), 
('Триллер'), 
('Документальный'), 
('Боевик');
