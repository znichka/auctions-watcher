CREATE SCHEMA IF NOT EXISTS bots_schema;

USE bots_schema;

CREATE TABLE IF NOT EXISTS managers (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  token VARCHAR(46) NOT NULL,
  chat_id VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS pages (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  url VARCHAR(500) NOT NULL,
  description VARCHAR(255) NOT NULL,
  period INT NOT NULL,
  notify INT
);

CREATE TABLE IF NOT EXISTS managers_pages (
   manager_description_id INT,
   pages_id INT
);

CREATE TABLE IF NOT EXISTS items (
   item_id VARCHAR(32) NOT NULL,
   manager_id INT NOT NULL,
   image_hash VARCHAR(64) NOT NULL,
   last_seen_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (image_hash, manager_id)
);