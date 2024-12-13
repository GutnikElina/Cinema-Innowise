CREATE TABLE `users` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `password` VARCHAR(512) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `role` ENUM('USER','ADMIN') NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `created_at` DATETIME NULL DEFAULT (CURRENT_TIMESTAMP),
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `username` (`username`) USING BTREE
);

CREATE TABLE `movies` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `year` VARCHAR(10) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `poster` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `plot` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `genre` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `director` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `actors` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `imdbRating` VARCHAR(10) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `runtime` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `film_session` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `movie_title` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `price` DECIMAL(10,2) NOT NULL,
    `date` DATE NOT NULL,
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL,
    `capacity` INT NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `ticket` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `session_id` INT NOT NULL,
    `seat_number` VARCHAR(10) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `purchase_time` DATETIME NULL DEFAULT (CURRENT_TIMESTAMP),
    `status` ENUM('PENDING','CONFIRMED','CANCELLED','RETURNED') NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `request_type` ENUM('PURCHASE','RETURN') NOT NULL DEFAULT 'PURCHASE' COLLATE 'utf8mb4_0900_ai_ci',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `fk_ticket_user` (`user_id`) USING BTREE,
    INDEX `fk_ticket_session` (`session_id`) USING BTREE,
    CONSTRAINT `fk_ticket_session` FOREIGN KEY (`session_id`) REFERENCES `film_session` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_ticket_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO `users` (`username`, `password`, `role`, `created_at`) VALUES
('admin', '$2a$10$R4o9QwMEPW9.YpctiGUsROhxmWd8U8/q5QlV/GE.erbKaXZgJ8sjm', 'ADMIN', '2024-12-13 08:10:49.658637'),
('user123', '$2a$10$QAmv0FYxfZkEBWVgWjlbjuEZRXAMMJcFQprqOmq0mpqT5fMNT4wPa', 'USER', CURRENT_TIMESTAMP);

INSERT INTO `film_session` (`movie_title`, `price`, `date`, `start_time`, `end_time`, `capacity`) VALUES
('The Matrix', 15.00, CURRENT_DATE, '12:00:00', '14:30:00', 100),
('Inception', 18.00, CURRENT_DATE, '15:30:00', '18:00:00', 80),
('Interstellar', 20.00, CURRENT_DATE, '19:00:00', '22:00:00', 120);

INSERT INTO `ticket` (`user_id`, `session_id`, `seat_number`, `status`, `request_type`) VALUES
(2, 1, '15', 'PENDING', 'PURCHASE'),
(2, 2, '16', 'CONFIRMED', 'PURCHASE'),
(2, 3, '25', 'PENDING', 'PURCHASE');