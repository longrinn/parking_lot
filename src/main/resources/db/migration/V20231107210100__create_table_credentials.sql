CREATE TABLE Credentials (
               id SERIAL PRIMARY KEY,
               email VARCHAR(255) UNIQUE NOT NULL CHECK (email LIKE '%_@__%.__%'),
               password VARCHAR(255) NOT NULL
    );