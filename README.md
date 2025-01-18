## Ticket Booking System

## Технологии:
- Frontend: JSP, HTML, CSS
- Backend: Java EE (Servlets, JSP), Hibernate
- База данных: MySQL
- Сервер: Apache Tomcat 10.0.21
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
  - При первичном поиске фильмов может долго загружаться, так как изначально данные подгружаются из API. 
После первого запроса данные будут браться из БД.

### Остановка приложения
```bash
docker-compose down
```

Для удаления всех данных (включая базу данных):
```bash
docker-compose down -v
```

## Текущий этап выполнения

- Панель администратора
- Панель пользователя

- Функционал администратора: управление пользователями, управление сеансами, управление билетами; поиск фильмов, подтверждение (отмена) 
билетов администратором, а также одобрение возврата при запросе пользователя.
Примечание: Каждое управление сущностями подразумевает: добавление, обновление, удаление, просмотр всех объектов, хранящихся в бд этой сущности.

- Функционал пользователя: редактирование своего аккаунта, поиск фильмов, покупка билетов пользователем и просмотр своих купленных билетов

- Поиск фильмов по названию. Изначально происходит поиск фильмов в базе данных, если фильмы с таким названием не найдены, 
то далее происходит поиск фильмов в OMDB API и подгружаются данные из API в БД. В дальнейшем при поиске фильмов с таким же названием 
данные будут получаться непосредственно из БД. 

***В разработке:*** добавление фильтрации и функции изменения языков(англ/рус).

# Этапы разработки и сроки

| Этап | Описание | Сроки | Статус     |
|------|----------|-------|------------|
| 1. Подготовка структуры проекта и создание базы данных | Создание базового каркаса проекта. Подготовка структуры Maven. Проектирование базы данных (таблицы для пользователей, сеансов, билетов). Написание SQL-скриптов для создания и заполнения таблиц. | 26.11-28.11 | Готово     |
| 2. Начало реализации работы администратора: управление пользователями | Создание JSP для списка пользователей и формы редактирования. Реализация сервлета для отображения списка и обработки запросов на добавление/удаление/редактирование пользователей. Создание фильтра доступа для страниц администрирования. | 29.11-01.12 | Готово     |
| 3. Управление сеансами и билетами со стороны администратора | Создание JSP для управления сеансами (добавление, редактирование, удаление, просмотр). Реализация сервлета для обработки запросов. Создание фильтра доступа для ограничения доступа к страницам только администраторам. | 02.12-05.12 | Готово     |
| 4. Начало реализации бизнес-логики процессов со стороны пользователя: покупка билетов и редактирование своего аккаунта | Создание JSP для отображения списка сеансов с возможностью покупки билетов. Реализация сервлета для обработки покупки и сохранения данных в базе. JSP и сервлет для редактирования профиля пользователя. | 06.12-08.12 | Готово     |
| 5. Просмотр купленных билетов и предстоящих сеансов для пользователя | Создание JSP для отображения списка купленных билетов. JSP для отображения доступных сеансов. Реализация сервлетов для обработки запросов и получения данных из базы. | 09.12-10.12 | Готово     |
| 6. Реализация работы авторизации и регистрации | Создание JSP для форм входа и регистрации. Реализация сервлета для обработки аутентификации и регистрации. Настройка фильтра доступа для ограничения доступа неавторизованных пользователей. | 11.12-13.12 | Готово     |
| 7. Устранение ошибок, улучшение оформления и добавление некоторого функционала (например, сортировка, поиск и т.д.) | Тестирование. Исправление багов. Улучшение визуального оформления JSP страниц. Оптимизация логики сервлетов. | 13.12-15.12 | Готово |

## Подробности этапов

### Общая структура:
- **Сущности:** 
  - `User` (пользователь): ID, логин, хэшированный пароль, роль (администратор/пользователь), дата создания аккаунта.
  - `Session` (сеанс): ID, фильм, дата, время начала сеанса, время конца сеанса, вместимость зала, цена.
  - `Ticket` (билет): ID, пользователь, сеанс, место, время покупки, статус покупки(в ожидании, подтвержден, не подтвержден, возвращен).
  - `Movie` (фильм): название, год выпуска, постер, сюжет, длительность, жанр, IMDB рейтинг.
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
