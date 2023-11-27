CREATE TABLE Working_Time (
                             id SERIAL PRIMARY KEY,
                             parking_lot_id INT NOT NULL REFERENCES Parking_Lot(id),
                             name_day VARCHAR(9) NOT NULL
);