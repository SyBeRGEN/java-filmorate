INSERT INTO  users SELECT * FROM (
SELECT  1, 'someuser@mail.ru', 'someuserlogin', 'someusername', '1984-10-10' UNION
SELECT  2, 'yandex_user@yandex.ru', 'yandex', 'yandexname', '1988-10-10' UNION
SELECT  3, 'gmail_user@gmail.com', 'gmail', 'gmailname', '1994-12-14'
) x WHERE NOT EXISTS (SELECT * FROM users);

