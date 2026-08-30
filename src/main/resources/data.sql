MERGE INTO mpa (mpa_id, mpa_name) KEY (mpa_id) VALUES (1, 'G');
MERGE INTO mpa (mpa_id, mpa_name) KEY (mpa_id) VALUES (2, 'PG');
MERGE INTO mpa (mpa_id, mpa_name) KEY (mpa_id) VALUES (3, 'PG_13');
MERGE INTO mpa (mpa_id, mpa_name) KEY (mpa_id) VALUES (4, 'R');
MERGE INTO mpa (mpa_id, mpa_name) KEY (mpa_id) VALUES (5, 'NC_17');

MERGE INTO genre (genre_id, genre_name) KEY (genre_id) VALUES (1, 'COMEDY');
MERGE INTO genre (genre_id, genre_name) KEY (genre_id) VALUES (2, 'DRAMA');
MERGE INTO genre (genre_id, genre_name) KEY (genre_id) VALUES (3, 'CARTOON');
MERGE INTO genre (genre_id, genre_name) KEY (genre_id) VALUES (4, 'THRILLER');
MERGE INTO genre (genre_id, genre_name) KEY (genre_id) VALUES (5, 'DOCUMENTARY');
MERGE INTO genre (genre_id, genre_name) KEY (genre_id) VALUES (6, 'ACTION');

INSERT INTO films (film_name, description, release_date, duration, mpa_id) VALUES ('Film 1', 'Description film 1', '2001-01-01', 100, 1);
INSERT INTO films (film_name, description, release_date, duration, mpa_id) VALUES ('Film 2', 'Description film 2', '2001-02-01', 102, 2);
INSERT INTO films (film_name, description, release_date, duration, mpa_id) VALUES ('Film 3', 'Description film 3', '2001-03-01', 103, 3);

INSERT INTO users (email, login, name, birthday_date) VALUES ('user1@bk.ru', 'user1login', 'name 1', '1991-01-01');
INSERT INTO users (email, login, name, birthday_date) VALUES ('user2@bk.ru', 'user2login', 'name 2', '1992-02-02');
INSERT INTO users (email, login, name, birthday_date) VALUES ('user3@bk.ru', 'user3login', 'name 3', '1993-03-03');

INSERT INTO liked_film_user (film_id, user_id) VALUES (1, 1);
INSERT INTO liked_film_user (film_id, user_id) VALUES (1, 2);
INSERT INTO liked_film_user (film_id, user_id) VALUES (1, 3);
INSERT INTO liked_film_user (film_id, user_id) VALUES (2, 2);
INSERT INTO liked_film_user (film_id, user_id) VALUES (2, 3);
INSERT INTO liked_film_user (film_id, user_id) VALUES (3, 1);

INSERT INTO film_genre (film_id, genre_id) VALUES (1, 1);
INSERT INTO film_genre (film_id, genre_id) VALUES (1, 4);
INSERT INTO film_genre (film_id, genre_id) VALUES (1, 3);
INSERT INTO film_genre (film_id, genre_id) VALUES (2, 2);
INSERT INTO film_genre (film_id, genre_id) VALUES (2, 5);
INSERT INTO film_genre (film_id, genre_id) VALUES (3, 6);

INSERT INTO friends (user_id, friend_id, is_confirmed) VALUES (1, 2, FALSE);
INSERT INTO friends (user_id, friend_id, is_confirmed) VALUES (2, 3, TRUE);
INSERT INTO friends (user_id, friend_id, is_confirmed) VALUES (3, 2, TRUE);
INSERT INTO friends (user_id, friend_id, is_confirmed) VALUES (2, 1, FALSE);