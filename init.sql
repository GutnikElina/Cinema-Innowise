CREATE TABLE `users`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `username`   VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `password`   VARCHAR(512) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `role`       ENUM('ROLE_USER','ROLE_ADMIN') NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `created_at` DATETIME NULL DEFAULT (CURRENT_TIMESTAMP),
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `username` (`username`) USING BTREE
);

CREATE TABLE `movies`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `title`      VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `year`       VARCHAR(10) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `poster`     VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `plot`       TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `genre`      VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `imdbRating` VARCHAR(10) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `runtime`    VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `film_session`
(
    `id`         BIGINT         NOT NULL AUTO_INCREMENT,
    `movie_id`   BIGINT         NOT NULL,
    `price`      DECIMAL(10, 2) NOT NULL,
    `date`       DATE           NOT NULL,
    `start_time` TIME           NOT NULL,
    `end_time`   TIME           NOT NULL,
    `capacity`   INT            NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE `ticket`
(
    `id`            BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT      NOT NULL,
    `session_id`    BIGINT      NOT NULL,
    `seat_number`   VARCHAR(10) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `purchase_time` DATETIME NULL DEFAULT (CURRENT_TIMESTAMP),
    `status`        ENUM('PENDING','CONFIRMED','CANCELLED','RETURNED') NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `request_type`  ENUM('PURCHASE','RETURN') NOT NULL DEFAULT 'PURCHASE' COLLATE 'utf8mb4_0900_ai_ci',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX           `fk_ticket_user` (`user_id`) USING BTREE,
    INDEX           `fk_ticket_session` (`session_id`) USING BTREE,
    CONSTRAINT `fk_ticket_session` FOREIGN KEY (`session_id`) REFERENCES `film_session` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_ticket_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO `users` (`username`, `password`, `role`, `created_at`) VALUES
   ('admin', '$2a$10$R4o9QwMEPW9.YpctiGUsROhxmWd8U8/q5QlV/GE.erbKaXZgJ8sjm', 'ROLE_ADMIN', '2024-12-13 08:10:49.658637'),
   ('user123', '$2a$10$QAmv0FYxfZkEBWVgWjlbjuEZRXAMMJcFQprqOmq0mpqT5fMNT4wPa', 'ROLE_USER', CURRENT_TIMESTAMP);

INSERT INTO `movies` (`title`, `year`, `poster`, `plot`, `genre`, `imdbRating`, `runtime`) VALUES
   ('Inception', '2010', 'https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg', 'A thief who enters the dreams of others.', 'Sci-Fi', '8.8', '148 min'),
   ('The Dark Knight', '2008', 'https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SX300.jpg', 'Batman faces Joker in Gotham.', 'Action', '9.0', '152 min'),
   ('Interstellar', '2014', 'https://m.media-amazon.com/images/M/MV5BYzdjMDAxZGItMjI2My00ODA1LTlkNzItOWFjMDU5ZDJlYWY3XkEyXkFqcGc@._V1_SX300.jpg', 'A group of explorers travel through a wormhole in space.', 'Sci-Fi', '8.6', '169 min'),
   ('Dune', '2021', 'https://m.media-amazon.com/images/M/MV5BNWIyNmU5MGYtZDZmNi00ZjAwLWJlYjgtZTc0ZGIxMDE4ZGYwXkEyXkFqcGc@._V1_SX300.jpg', 'Paul Atreides arrives on Arrakis after his father accepts the stewardship of the dangerous planet. However, chaos ensues after a betrayal as forces clash to control melange, a precious resource.', 'Action', '8.0', '155 min'),
   ('The Green Mile', '1999', 'https://m.media-amazon.com/images/M/MV5BMTUxMzQyNjA5MF5BMl5BanBnXkFtZTYwOTU2NTY3._V1_SX300.jpg', 'Paul Edgecomb, the head guard of a prison, meets an inmate, John Coffey, a black man who is accused of murdering two girls. His life changes drastically when he discovers that John has a special gift.', 'Crime', '8.6', '189 min');

INSERT INTO `film_session` (`movie_id`, `price`, `date`, `start_time`, `end_time`, `capacity`) VALUES
   (1, 10.00, '2024-02-10', '18:00:00', '20:30:00', 70),
   (2, 12.00, '2024-02-11', '19:00:00', '21:30:00', 80),
   (3, 11.00, '2024-02-12', '20:00:00', '22:50:00', 100),
   (4, 9.50, '2024-02-13', '17:30:00', '19:45:00', 90),
   (5, 10.50, '2024-02-14', '21:00:00', '23:35:00', 85);

INSERT INTO `ticket` (`user_id`, `session_id`, `seat_number`, `status`, `request_type`) VALUES
   (1, 1, '10', 'PENDING', 'PURCHASE'),
   (2, 2, '15', 'CONFIRMED', 'PURCHASE'),
   (1, 3, '22', 'PENDING', 'PURCHASE'),
   (2, 4, '5', 'CANCELLED', 'PURCHASE'),
   (2, 5, '30', 'PENDING', 'PURCHASE'),
   (2, 3, '45', 'PENDING', 'PURCHASE'),
   (1, 4, '12', 'RETURNED', 'PURCHASE'),
   (2, 5, '18', 'PENDING', 'PURCHASE');
