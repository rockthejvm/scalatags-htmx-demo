CREATE TABLE contacts
(
    id    INTEGER PRIMARY KEY,
    name  TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE
);