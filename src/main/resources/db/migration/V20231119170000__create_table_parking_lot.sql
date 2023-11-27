CREATE TABLE Parking_Lot (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(70) NOT NULL UNIQUE,
                            address VARCHAR(70) NOT NULL UNIQUE,
                            start_time TIME NOT NULL,
                            end_time TIME NOT NULL,
                            state BOOLEAN DEFAULT true
);