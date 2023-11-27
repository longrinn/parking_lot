CREATE TABLE user_parking_lot (
                                  user_id INT,
                                  parking_lot_id INT,
                                  PRIMARY KEY (user_id, parking_lot_id),
                                  FOREIGN KEY (user_id) REFERENCES client(id),
                                  FOREIGN KEY (parking_lot_id) REFERENCES parking_lot(id)
);