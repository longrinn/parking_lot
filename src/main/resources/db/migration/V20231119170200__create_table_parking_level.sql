CREATE TABLE Parking_Level (
                              id SERIAL PRIMARY KEY,
                              parking_lot_id INT NOT NULL REFERENCES Parking_Lot(id),
                              floor INT NOT NULL DEFAULT 1,
                              total_spots INT NOT NULL
);