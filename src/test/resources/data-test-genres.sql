INSERT INTO genres SELECT * FROM (
SELECT  1, 'Комедия' UNION
SELECT  2, 'Драма' UNION
SELECT  3, 'Мультфильм' UNION
SELECT  4, 'Триллер' UNION
SELECT  5, 'Документальный' UNION
SELECT  6, 'Боевик') x WHERE NOT EXISTS (SELECT * FROM genres);


