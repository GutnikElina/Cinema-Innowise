## Ticket Booking System

## Технологии:
- Frontend: JSP, HTML, CSS
- Backend: Java EE (Servlets, JSP), Hibernate
- База данных: MySQL
- Сервер: Apache Tomcat 10.0.21
- Инструменты: Maven, Git для контроля версий, JUnit для тестирования
  
= Общий срок разработки: 3 недели (26.11.2024 — 15.12.2024).

## Текущий этап выполнения - 4

- Панель администратора
- Функционал администратора: управление пользователями, управление сеансами, управление билетами.
Каждое управление сущностями подразумевает: добавление, обновление, удаление, просмотр всех хранящихся в бд объектов этой сущности.
- Поиск фильмов по названию (с помощью использования OMDB API). При успешном поиске выводятся постеры, название, жанр и рейтинг найденных фильмов.
- Подтверждение (отмена) билетов администратором, а также одобрение возврата при запросе пользователя.

***В разработке:*** перенос бизнес-логики проекта из сервлетов в сервисы, коррекция обработки исключений и валидации, создание dto классов.

# Этапы разработки и сроки

| Этап | Описание | Сроки | Статус         |
|------|----------|-------|----------------|
| 1. Подготовка структуры проекта и создание базы данных | Создание базового каркаса проекта. Подготовка структуры Maven. Проектирование базы данных (таблицы для пользователей, сеансов, билетов). Написание SQL-скриптов для создания и заполнения таблиц. | 26.11-28.11 | Готово         |
| 2. Начало реализации работы администратора: управление пользователями | Создание JSP для списка пользователей и формы редактирования. Реализация сервлета для отображения списка и обработки запросов на добавление/удаление/редактирование пользователей. Создание фильтра доступа для страниц администрирования. | 29.11-01.12 | Готово         |
| 3. Управление сеансами и билетами со стороны администратора | Создание JSP для управления сеансами (добавление, редактирование, удаление, просмотр). Реализация сервлета для обработки запросов. Создание фильтра доступа для ограничения доступа к страницам только администраторам. | 02.12-05.12 | Готово         |
| 4. Начало реализации бизнес-логики процессов со стороны пользователя: покупка билетов и редактирование своего аккаунта | Создание JSP для отображения списка сеансов с возможностью покупки билетов. Реализация сервлета для обработки покупки и сохранения данных в базе. JSP и сервлет для редактирования профиля пользователя. | 06.12-08.12 | В процессе |
| 5. Просмотр купленных билетов и предстоящих сеансов для пользователя | Создание JSP для отображения списка купленных билетов. JSP для отображения доступных сеансов. Реализация сервлетов для обработки запросов и получения данных из базы. | 09.12-10.12 | Ожидает начала |
| 6. Реализация работы авторизации и регистрации | Создание JSP для форм входа и регистрации. Реализация сервлета для обработки аутентификации и регистрации. Настройка фильтра доступа для ограничения доступа неавторизованных пользователей. | 11.12-13.12 | Готово |
| 7. Устранение ошибок, улучшение оформления и добавление некоторого функционала (например, сортировка, поиск и т.д.) | Тестирование. Исправление багов. Улучшение визуального оформления JSP страниц. Оптимизация логики сервлетов. | 13.12-15.12 | Ожидает начала |

## Подробности этапов

### Общая структура:
- **Сущности:** 
  - `User` (пользователь): ID, логин, хэшированный пароль, роль (администратор/пользователь), дата создания аккаунта.
  - `Session` (сеанс): ID, фильм, дата, время начала сеанса, время конца сеанса, вместимость зала, цена.
  - `Ticket` (билет): ID, пользователь, сеанс, место, время покупки, статус покупки(в ожидании, подтвержден, не подтвержден, возвращен).
  - `Movie` (фильм): название, год выпуска, постер, сюжет, длительность, жанр, IMDB рейтинг, директор, актеры.
- **JSP страницы:** 
  - Для администрирования: главная страница админа, управление пользователями, сеансами, билетами.
  - Для пользователя: регистрация, авторизация, просмотр своих билетов, покупка билетов, редактирование своего профиля.
- **Сервлеты:** 
  - Обработка главной страницы администратора.
  - Обработка главной страницы пользователя.
  - Обработка CRUD-операций для каждой сущности (со стороны администратора).
  - Логика авторизации/регистрации.
  - Работа с покупками билетов (для пользователей).
  - Работа с подтверждением покупки билетов (для администратора).
  - Обработка страницы для просмотра купленных билетов пользователя.
  - Обработка страницы редактирования профиля пользователя.
- **Фильтры доступа:** 
  - Разделение прав доступа для администратора и обычного пользователя.

### Примечание:
Это минимальная версия проекта. В процессе разработки возможны дополнения и расширение функционала.
На каждом этапе предполагается активная работа с базой данных, настройка логики на стороне сервлетов и обработка взаимодействия через JSP.
