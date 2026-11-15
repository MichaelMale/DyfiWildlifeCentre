-- Baseline schema for Dyfi Wildlife Centre

CREATE SEQUENCE IF NOT EXISTS points_of_interest_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS points_of_interest
(
    id                   BIGINT           NOT NULL PRIMARY KEY,
    description          VARCHAR(255),
    latitude             DOUBLE PRECISION NOT NULL,
    longitude            DOUBLE PRECISION NOT NULL,
    name                 VARCHAR(255)     NOT NULL,
    postcode             VARCHAR(255),
    distance_from_centre DOUBLE PRECISION,
    category             VARCHAR(255),
    description_cy       VARCHAR(255),
    name_cy              VARCHAR(255),
    image_url            VARCHAR(2048),
    station_code         VARCHAR(8),
    bus_stop_code        VARCHAR(24)
);

CREATE TABLE IF NOT EXISTS roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(32)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users_roles
(
    users_id BIGINT NOT NULL PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    roles_id BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE
);
