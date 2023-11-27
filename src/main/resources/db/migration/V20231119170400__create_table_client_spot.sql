CREATE TABLE Client_Spot (
                          spot_id INT NOT NULL REFERENCES Parking_Spot(id),
                          user_id INT NOT NULL REFERENCES Client(id),
                          PRIMARY KEY (spot_id, user_id)
);