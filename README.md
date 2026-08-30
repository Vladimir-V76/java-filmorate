# java-filmorate
Template repository for Filmorate project.

Ссылка на ER-диаграмму БД Filmorate для проверки: https://github.com/Vladimir-V76/java-filmorate/blob/add-friends-likes/Filmorate%20Diagram.png

![ER-диаграмма](Filmorate%20Diagram.png)

Таблица films содержит основную информацию об фильме в базе данных. Первичный ключ film_id. 

Так как согласно ТЗ, помимо указанной информации, необходимо хранить сведения о пользователях, которые «лайкнули» фильм (т.е. больше одного), то, для реализации данного требования, предлагается использовать таблицу liked_film_user. 

В данной таблице первичный ключ составной (id и film_id) для обеспечения уникальности записей в таблице. 

Такая же ситуация с хранением информации по жанрам фильмов – таблица film_genre.

Информация о жанрах и рейтингах хранятся в отдельных таблицах genre и rating соотвественно.
Использование операторов JOIN позволит получать информацию о фильмах по id, в том числе получать различные списки.

Таблица users содержит основную информацию о пользователе. 

Таблица friends хранит информацию о пользователях, которым отправлен запрос на добавление в друзья. Поле is_confirmated логическое и будет содержать информацию о том, принято приглашение или нет. Первичный ключ в данной таблице также составной.

## Примерные запросы для получения информации из БД
1. Получить 5 самых полулярных фильмов 
```sql
SELECT f.name, COUNT(lfu.user_id) as count_like
FROM films AS f
LEFT JOIN liked_film_user AS lfu ON lfu.film_id = f.film_id
GROUP BY f.name
ORDER BY count_like DESC
LIMIT 5;
```
2. Получение списка всех пользователей
```sql
SELECT *
FROM users AS u
LEFT JOIN friends AS f ON u.user_id=f.user_id
```
3.Получение списка всех фильмов
```sql
SELECT *
FROM films AS f
LEFT JOIN film_genre AS fg ON f.film_id=fg.film_id
LEFT JOIN liked_film_user AS lfu ON f.film_id=lfu.film_id
```
4.Получение фильма по id=1
```sql
SELECT f.name
FROM films AS f
LEFT JOIN film_genre AS fg ON f.film_id=fg.film_id
LEFT JOIN liked_film_user AS lfu ON f.film_id=lfu.film_id
WHERE f.films_id=1
```
5. Получение списка друзей пользователя с id=1
```sql
SELECT *
FROM users AS u
JOIN friends AS f ON u.user_id=f.user_id
WHERE f.user_id=1
```
6.Получение списка общих друзей пользователей id=1 и id=2
```sql
SELECT *
FROM (SELECT *
	FROM users AS u
	JOIN friends AS f ON u.user_id=f.user_id
	WHERE f.user_id=1) AS friend1
WHERE friend1.friend_id=2
```
