CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS friend_requests CASCADE;
DROP TABLE IF EXISTS users_cars CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS car_models CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS auth_credentials CASCADE;

CREATE TABLE auth_credentials (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  email VARCHAR(255) UNIQUE NOT NULL,
                                  password TEXT,
                                  provider VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
                                  google_id TEXT
);

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       auth_credential_id UUID UNIQUE NOT NULL REFERENCES auth_credentials(id) ON DELETE CASCADE,
                       profile_picture_path TEXT,
                       full_name VARCHAR(150) NOT NULL,
                       phone_number VARCHAR(20),
                       birth_date DATE NOT NULL,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       country VARCHAR(50) NOT NULL,
                       spot_score INTEGER NOT NULL DEFAULT 0,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE car_models (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            brand VARCHAR(50) NOT NULL,
                            model VARCHAR(50) NOT NULL,
                            start_year INTEGER NOT NULL,
                            end_year INTEGER NOT NULL,
                            UNIQUE(brand, model)
);

CREATE TABLE posts (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       car_model_id UUID REFERENCES car_models(id) ON DELETE NO ACTION,
                       image_path TEXT NOT NULL,
                       description TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comments (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                          comment_text TEXT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE friend_requests (
                                 sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 receiver_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (sender_id, receiver_id)
);

CREATE TABLE friends (
                         user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         friend_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (user_id, friend_id),
                         CONSTRAINT chk_no_self_friendship CHECK (user_id <> friend_id)
);

CREATE TABLE likes (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       UNIQUE(user_id, post_id)
);

CREATE TABLE users_cars (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                            car_model_id UUID NOT NULL REFERENCES car_models(id) ON DELETE CASCADE,
                            image_path TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);