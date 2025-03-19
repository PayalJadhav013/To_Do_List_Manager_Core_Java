CREATE DATABASE todolist_db;

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    status VARCHAR(50) DEFAULT 'Pending'
);

ALTER TABLE tasks
ADD COLUMN completion_date DATE;

SELECT * FROM tasks;