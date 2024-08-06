CREATE TABLE users
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    username     VARCHAR(255),
    password     VARCHAR(255),
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255),
    image_url    TEXT,
    sub          VARCHAR(255),
    service_type TINYINT      NOT NULL,
    role         TINYINT      NOT NULL,
    status       TINYINT      NOT NULL DEFAULT 0,
    created_at   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);