CREATE TABLE auth_credentials (
                                  id SERIAL PRIMARY KEY,
                                  email VARCHAR(255) UNIQUE NOT NULL,
                                  password TEXT,
                                  provider VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
                                  google_id VARCHAR(100)
);


CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       auth_credential_id INTEGER UNIQUE NOT NULL,
                       profile_picture_path TEXT,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       birth_date DATE NOT NULL,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       country VARCHAR(50) NOT NULL,
                       spot_score INTEGER NOT NULL DEFAULT 0,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_auth_credential
                           FOREIGN KEY (auth_credential_id) REFERENCES auth_credentials(id) ON DELETE CASCADE
);

CREATE TABLE car_models (
                            id SERIAL PRIMARY KEY,
                            brand VARCHAR(50) NOT NULL,
                            model VARCHAR(50) NOT NULL,
                            year INTEGER
);

CREATE UNIQUE INDEX ux_car_models_brand_model_year ON car_models (brand, model, year);

CREATE TABLE users_cars (
                            id SERIAL PRIMARY KEY,
                            user_id INTEGER UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                            car_model_id INTEGER REFERENCES car_models(id) ON DELETE CASCADE,
                            image_path TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE posts (
                       id SERIAL PRIMARY KEY,
                       user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       car_model_id INTEGER NOT NULL REFERENCES car_models(id) ON DELETE NO ACTION,
                       image_path TEXT NOT NULL,
                       description TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE friends (
                         user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         friend_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT friends_pkey PRIMARY KEY (user_id, friend_id),
                         CONSTRAINT chk_no_self_friendship CHECK (user_id <> friend_id)
);



CREATE TABLE likes (
                       id SERIAL PRIMARY KEY,
                       user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       post_id INTEGER NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT likes_user_post_unique UNIQUE (user_id, post_id)
);


CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          post_id INTEGER NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                          comment_text TEXT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE friend_requests (
                                 sender_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 receiver_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (sender_id, receiver_id)
);



