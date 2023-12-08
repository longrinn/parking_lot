CREATE OR REPLACE FUNCTION get_level_letter(floor_number INT)
    RETURNS VARCHAR(1) AS $$
BEGIN
    RETURN CHR(floor_number + 64);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_parking_spot_name()
    RETURNS TRIGGER AS $$
DECLARE
    level_letter VARCHAR(1);
    spot_number INT;
    new_spot_name VARCHAR(5);
BEGIN
    SELECT get_level_letter((SELECT floor FROM Parking_Level WHERE id = NEW.level_id)) INTO level_letter;
    SELECT COALESCE(MAX(CAST(SUBSTRING(name FROM '\d+') AS INTEGER)), 0)
    INTO spot_number

    FROM Parking_Spot
    WHERE level_id = NEW.level_id;

    spot_number := spot_number + 1;
    new_spot_name := level_letter || '-' || LPAD(CAST(spot_number AS VARCHAR), 3, '0');
    NEW.name := new_spot_name;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER parking_spot_before_insert
    BEFORE INSERT ON Parking_Spot
    FOR EACH ROW EXECUTE FUNCTION set_parking_spot_name();