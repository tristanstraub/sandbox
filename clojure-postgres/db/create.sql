CREATE DATABASE sandbox;

CREATE ROLE app_group;

\connect sandbox;

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(20)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON users TO GROUP app_group;
GRANT SELECT, UPDATE ON users_id_seq TO GROUP app_group;

CREATE USER app_user WITH PASSWORD 'password' IN GROUP app_group;
