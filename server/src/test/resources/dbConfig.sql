CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       profile_picture_path TEXT,
                       first_name VARCHAR(80) NOT NULL,
                       last_name VARCHAR(80) NOT NULL,
                       birth_date DATE NOT NULL,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password TEXT NOT NULL,
                       country VARCHAR(50) NOT NULL,
                       spot_score INT DEFAULT 0 NOT NULL
);

CREATE TABLE car_models (
                            id SERIAL PRIMARY KEY,
                            brand VARCHAR(50) NOT NULL,
                            model VARCHAR(50) NOT NULL,
                            year INT,
                            UNIQUE (brand, model, year) 
);

CREATE TABLE users_cars (
                      id SERIAL PRIMARY KEY,
                      user_id INT UNIQUE NOT NULL,
                      car_model_id INT NOT NULL,
                      image_path TEXT,
                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                      FOREIGN KEY (car_model_id) REFERENCES car_models(id)
);

CREATE TABLE posts (
                       id SERIAL PRIMARY KEY,
                       user_id INT NOT NULL,
                       car_model_id INT NOT NULL,
                       image_path TEXT NOT NULL,
                       description TEXT,
                       timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                       FOREIGN KEY (car_model_id) REFERENCES car_models(id)
);

CREATE TABLE friends (
                         user_id INT NOT NULL,
                         friend_id INT NOT NULL,
                         PRIMARY KEY (user_id, friend_id),
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT chk_no_self_friendship CHECK (user_id <> friend_id)
);

CREATE TABLE friend_requests (
                                 sender_id INT NOT NULL,
                                 receiver_id INT NOT NULL,
                                 PRIMARY KEY (sender_id, receiver_id),
                                 FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
                                 FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
                                 CHECK (sender_id <> receiver_id)
);

CREATE TABLE likes (
                       id SERIAL PRIMARY KEY,
                       user_id INT NOT NULL,
                       post_id INT NOT NULL,
                       timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                       FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                       UNIQUE(user_id, post_id)  
);

CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          user_id INT NOT NULL,
                          post_id INT NOT NULL,
                          comment_text TEXT NOT NULL,
                          timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);


