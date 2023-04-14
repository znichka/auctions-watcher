CREATE SCHEMA IF NOT EXISTS bots_schema;

USE bots_schema;

CREATE TABLE IF NOT EXISTS managers (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  token VARCHAR(255) NOT NULL,
  chat_id VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS pages (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  url VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  period INT NOT NULL,
  notify INT
);

CREATE TABLE IF NOT EXISTS managers_pages (
   manager_description_id INT,
   pages_id INT
   FOREIGN KEY (manager_description_id) REFERENCES managers(id),
   FOREIGN KEY (pages_id) REFERENCES pages(id)
);


CREATE TABLE IF NOT EXISTS items (
   item_id VARCHAR(255) NOT NULL,
   manager_id INT NOT NULL,
   image_hash VARCHAR(255) NOT NULL,
   PRIMARY KEY (item_id, image_hash, manager_id)
);