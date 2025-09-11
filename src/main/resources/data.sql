INSERT INTO rating (rating_id, name)
SELECT 1, 'G' FROM rating WHERE NOT EXISTS (SELECT 1 FROM rating WHERE rating_id = 1)
UNION ALL
SELECT 2, 'PG' FROM rating WHERE NOT EXISTS (SELECT 1 FROM rating WHERE rating_id = 2)
UNION ALL
SELECT 3, 'PG-13' FROM rating WHERE NOT EXISTS (SELECT 1 FROM rating WHERE rating_id = 3)
UNION ALL
SELECT 4, 'R' FROM rating WHERE NOT EXISTS (SELECT 1 FROM rating WHERE rating_id = 4);


INSERT INTO genres (genre_id, name)
SELECT 1, 'Комедия' FROM genres WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 1)
UNION ALL
SELECT 2, 'Драма' FROM genres WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 2)
UNION ALL
SELECT 3, 'Мультфильм' FROM genres WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 3)
UNION ALL
SELECT 4, 'Триллер' FROM genres WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 4)
UNION ALL
SELECT 5, 'Документальный' FROM genres WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 5)
UNION ALL
SELECT 6, 'Боевик' FROM genres WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 6);


INSERT INTO users (user_id, email, login, name, birthday)
SELECT 1, 'ivanov@mail.ru', 'ivan_ivanov', 'Иван Иванов', '1990-05-15' FROM users WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id = 1)
UNION ALL
SELECT 2, 'petrov@mail.ru', 'petr_petrov', 'Петр Петров', '1985-08-20' FROM users WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id = 2)
UNION ALL
SELECT 3, 'sidorova@mail.ru', 'maria_sidorova', 'Мария Сидорова', '1992-12-10' FROM users WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id = 3)
UNION ALL
SELECT 4, 'smirnov@mail.ru', 'alex_smirnov', 'Алексей Смирнов', '1988-03-25' FROM users WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id = 4);


INSERT INTO films (film_id, name, description, duration, release_date, rating_id)
SELECT 1, 'Интерстеллар', 'Фантастический эпос о путешествии к червоточинам', 169, '2014-11-06', 3 FROM films WHERE NOT EXISTS (SELECT 1 FROM films WHERE film_id = 1)
UNION ALL
SELECT 2, 'Начало', 'Триллер о внедрении в подсознание', 148, '2010-07-16', 3 FROM films WHERE NOT EXISTS (SELECT 1 FROM films WHERE film_id = 2)
UNION ALL
SELECT 3, 'Король Лев', 'Мультфильм о приключениях львенка Симбы', 88, '1994-06-24', 1 FROM films WHERE NOT EXISTS (SELECT 1 FROM films WHERE film_id = 3)
UNION ALL
SELECT 4, 'Крестный отец', 'Криминальная драма о семье мафиози', 175, '1972-03-24', 4 FROM films WHERE NOT EXISTS (SELECT 1 FROM films WHERE film_id = 4);


INSERT INTO genre_film (genre_id, film_id)
SELECT 2, 1 FROM genre_film WHERE NOT EXISTS (SELECT 1 FROM genre_film WHERE genre_id = 2 AND film_id = 1)
UNION ALL
SELECT 6, 1 FROM genre_film WHERE NOT EXISTS (SELECT 1 FROM genre_film WHERE genre_id = 6 AND film_id = 1)
UNION ALL
SELECT 4, 2 FROM genre_film WHERE NOT EXISTS (SELECT 1 FROM genre_film WHERE genre_id = 4 AND film_id = 2)
UNION ALL
SELECT 6, 2 FROM genre_film WHERE NOT EXISTS (SELECT 1 FROM genre_film WHERE genre_id = 6 AND film_id = 2)
UNION ALL
SELECT 3, 3 FROM genre_film WHERE NOT EXISTS (SELECT 1 FROM genre_film WHERE genre_id = 3 AND film_id = 3)
UNION ALL
SELECT 2, 3 FROM genre_film WHERE NOT EXISTS (SELECT 1 FROM genre_film WHERE genre_id = 2 AND film_id = 3)
UNION ALL
SELECT 2, 4 FROM genre_film WHERE NOT EXISTS (SELECT 1 FROM genre_film WHERE genre_id = 2 AND film_id = 4)
UNION ALL
SELECT 4, 4 FROM genre_film WHERE NOT EXISTS (SELECT 1 FROM genre_film WHERE genre_id = 4 AND film_id = 4);


INSERT INTO friendship (user_id, friend_id, status)
SELECT 1, 2, 1 FROM friendship WHERE NOT EXISTS (SELECT 1 FROM friendship WHERE user_id = 1 AND friend_id = 2)
UNION ALL
SELECT 1, 3, 0 FROM friendship WHERE NOT EXISTS (SELECT 1 FROM friendship WHERE user_id = 1 AND friend_id = 3)
UNION ALL
SELECT 3, 4, 1 FROM friendship WHERE NOT EXISTS (SELECT 1 FROM friendship WHERE user_id = 3 AND friend_id = 4);


INSERT INTO film_likes (user_id, film_id)
SELECT 1, 1 FROM film_likes WHERE NOT EXISTS (SELECT 1 FROM film_likes WHERE user_id = 1 AND film_id = 1)
UNION ALL
SELECT 1, 2 FROM film_likes WHERE NOT EXISTS (SELECT 1 FROM film_likes WHERE user_id = 1 AND film_id = 2)
UNION ALL
SELECT 2, 1 FROM film_likes WHERE NOT EXISTS (SELECT 1 FROM film_likes WHERE user_id = 2 AND film_id = 1)
UNION ALL
SELECT 3, 3 FROM film_likes WHERE NOT EXISTS (SELECT 1 FROM film_likes WHERE user_id = 3 AND film_id = 3)
UNION ALL
SELECT 4, 4 FROM film_likes WHERE NOT EXISTS (SELECT 1 FROM film_likes WHERE user_id = 4 AND film_id = 4)
UNION ALL
SELECT 2, 3 FROM film_likes WHERE NOT EXISTS (SELECT 1 FROM film_likes WHERE user_id = 2 AND film_id = 3);