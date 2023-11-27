ALTER TABLE Parking_Level
    ADD CONSTRAINT check_total_spots_range CHECK (total_spots BETWEEN 1 AND 150);

ALTER TABLE Parking_Level
    ADD CONSTRAINT check_floor_limit CHECK (floor >= 1 and floor <= 5);