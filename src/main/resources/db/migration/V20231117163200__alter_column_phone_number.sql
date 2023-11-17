ALTER TABLE Client DROP COLUMN phone;

ALTER TABLE Client
    ADD COLUMN phone VARCHAR(9);

ALTER TABLE Client
    ADD CONSTRAINT phone CHECK (phone ~ '^[0-9]{9}$');