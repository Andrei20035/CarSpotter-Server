ALTER TABLE car_models
    DROP COLUMN year;

ALTER TABLE car_models
    ADD COLUMN start_year INT,
    ADD COLUMN end_year INT;