CREATE TABLE Parking_Spot (
                              id SERIAL PRIMARY KEY,
                              level_id INT NOT NULL REFERENCES Parking_Level(id),
                              name VARCHAR(5) NOT NULL,
                              state BOOLEAN DEFAULT true,
                              type VARCHAR(30) NOT NULL DEFAULT 'regular'
);