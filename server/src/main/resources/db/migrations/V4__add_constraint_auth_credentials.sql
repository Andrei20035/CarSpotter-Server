ALTER TABLE auth_credentials
    ADD CONSTRAINT provider_consistency_check
        CHECK (
            (provider = 'REGULAR' AND google_id IS NULL AND password IS NOT NULL) OR
            (provider = 'GOOGLE' AND google_id IS NOT NULL AND password IS NULL)
            );