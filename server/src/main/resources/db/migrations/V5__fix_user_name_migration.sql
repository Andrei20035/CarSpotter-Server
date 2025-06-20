-- Fix the user name migration to handle existing data properly

-- First, add the columns back if they were dropped
ALTER TABLE users ADD COLUMN first_name VARCHAR(50);
ALTER TABLE users ADD COLUMN last_name VARCHAR(50);

-- Drop the problematic full_name column
ALTER TABLE users DROP COLUMN full_name;

-- Add full_name as nullable first
ALTER TABLE users ADD COLUMN full_name VARCHAR(100);

-- Populate full_name from existing data (if any)
UPDATE users
SET full_name = CONCAT_WS(' ', first_name, last_name)
WHERE full_name IS NULL;

-- Set NOT NULL constraint
ALTER TABLE users ALTER COLUMN full_name SET NOT NULL;

-- Clean up old columns
ALTER TABLE users DROP COLUMN first_name;
ALTER TABLE users DROP COLUMN last_name;