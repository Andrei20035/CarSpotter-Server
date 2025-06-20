-- Remove old name fields
ALTER TABLE users DROP COLUMN first_name;
ALTER TABLE users DROP COLUMN last_name;

-- Add new full name and phone number
ALTER TABLE users ADD COLUMN full_name VARCHAR(100) NOT NULL;
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);