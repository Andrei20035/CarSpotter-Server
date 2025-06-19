-- Add new full name and phone number
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);

-- Step 1: Add full_name as a nullable column
ALTER TABLE users ADD COLUMN full_name VARCHAR(100);

-- Step 2: Populate full_name from first_name and last_name
UPDATE users
SET full_name = CONCAT_WS(' ', first_name, last_name)
WHERE full_name IS NULL;-- Handles NULLs cleanly

-- Step 3: Set NOT NULL constraint after full_name is filled
ALTER TABLE users ALTER COLUMN full_name SET NOT NULL;

-- Step 4: Drop the old columns
ALTER TABLE users DROP COLUMN first_name;
ALTER TABLE users DROP COLUMN last_name;