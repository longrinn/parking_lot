CREATE TABLE Client (
            id INT NOT NULL REFERENCES Credentials(id),
            name VARCHAR(30) NOT NULL,
            phone CHAR(12) CHECK (phone LIKE '+373________'),
            role INT DEFAULT 2 REFERENCES Role(id)
    );