## TICKET BOOKING SYSTEM

## Краткое описание
Система бронирования билетов для кинотеатра, позволяющая пользователям искать фильмы, бронировать 
билеты, просматривать купленные билеты и управлять своим аккаунтом, а администраторам — управлять 
пользователями, сеансами и билетами.

## Технологии:
- Frontend: JSP, HTML, CSS
- Backend: Spring Framework/Data/MVC/Security, Hibernate
- База данных: MySQL
- Сервер: Apache Tomcat 11.0.1
- Инструменты: Maven, Git для контроля версий, JUnit для тестирования, Docker для контейнеризации
  
## Запуск проекта

### Предварительные требования
- Git
- Docker
- Docker Compose

### Шаги для запуска

1. Клонирование репозитория:
```bash
git clone https://github.com/GutnikElina/Cinema-Innowise.git
cd Cinema-Innowise
```

2. Запуск приложения:
```bash
docker-compose up -d
```

3. Проверка статуса:
```bash
docker-compose ps
```

4. Доступ к приложению:
- Веб-интерфейс: http://localhost:8080
- База данных: localhost:3307
  - Username: cinema_user
  - Password: cinema_password
  - Database: cinema_db

5. Вход:
- Пользователь:
  - login: user123
  - password: user123
- Администратор:
  - login: admin
  - password: admin

ПРИМЕЧАНИЕ:
  - При первом поиске фильмов может быть задержка, так как данные подгружаются из внешнего API. 
После первого запроса данные будут кешироваться в базе данных.

### Остановка приложения
```bash
docker-compose down
```

Для удаления всех данных (включая базу данных):
```bash
docker-compose down -v
```

## Функциональные возможности

**Администратор:**
- Управление пользователями: добавление, обновление, удаление пользователей.
- Управление сеансами: создание, редактирование, удаление сеансов.
- Управление билетами: подтверждение и отмена билетов.
- Поиск фильмов: поиск по названию с использованием базы данных или OMDB API, с последующей синхронизацией данных в базе данных.
- Одобрение возврата билетов.

**Пользователь:**
- Аккаунт пользователя: регистрация, авторизация, редактирование профиля.
- Поиск фильмов: поиск по названию.
- Бронирование билетов: покупка и просмотр собственных билетов.
- История покупок: просмотр купленных билетов с возможностью отмены.

## Подробности этапов

### Общая структура:
- **Сущности:** 
  - `User` (пользователь): ID, логин, хэшированный пароль, роль (администратор/пользователь), дата создания аккаунта.
  - `Session` (киносеанс): ID, фильм, дата, время начала сеанса, время конца сеанса, вместимость зала, цена.
  - `Ticket` (билет): ID, пользователь, сеанс, место, время покупки, статус покупки(в ожидании, подтвержден, не подтвержден, возвращен).
  - `Movie` (фильм): название, год выпуска, постер, сюжет, длительность, жанр, IMDB рейтинг.
- **JSP страницы:** 
  - Для администрирования: главная страница админа, управление пользователями, сеансами, билетами, подтверждение/отказ билетов.
  - Для пользователя: регистрация, авторизация, главная страница пользователя, просмотр своих билетов, покупка билетов, редактирование своего профиля.
- **Контроллеры:** 
  - CRUD-операции для сущностей User, Ticket, Session.
  - Обработка авторизации и регистрации.
  - Логика обработки билетов (покупка, возврат, подтверждение).
  - Логика работы с поиском фильмов через базу данных или API.
- **Фильтры доступа (с помощью Spring Security):** 
  - Разделение прав доступа для администратора и обычного пользователя.
  - Поддержка нескольких языков (английский, русский).

### Примечание:
Это минимальная версия проекта. В процессе разработки возможны дополнения и расширение функционала.
Активное использование базы данных: все изменения будут отражаться в базе данных, через сервисы и контроллеры.