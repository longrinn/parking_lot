CREATE OR REPLACE FUNCTION get_level_letter(level_number INT)
    RETURNS VARCHAR(1) AS $$
BEGIN
    RETURN CHR(level_number + 64);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_parking_spot_name()
    RETURNS TRIGGER AS $$
DECLARE
    level_letter VARCHAR(1);
    spot_number INT;
    new_spot_name VARCHAR(5);
BEGIN
    SELECT get_level_letter(NEW.level_id) INTO level_letter;

    spot_number := 1;

    new_spot_name := level_letter || '-' || LPAD(CAST(spot_number AS VARCHAR), 3, '0');

    WHILE EXISTS (
        SELECT 1 FROM Parking_Spot
        WHERE name = new_spot_name
    ) LOOP
            spot_number := spot_number + 1;
            new_spot_name := level_letter || '-' || LPAD(CAST(spot_number AS VARCHAR), 3, '0');
        END LOOP;
    NEW.name := new_spot_name;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER parking_spot_before_insert
    BEFORE INSERT ON Parking_Spot
    FOR EACH ROW EXECUTE FUNCTION set_parking_spot_name();