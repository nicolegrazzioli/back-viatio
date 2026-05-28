CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       profile_image VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trips (
                       id UUID PRIMARY KEY,
                       user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       title VARCHAR(255) NOT NULL,
                       start_date DATE NOT NULL,
                       end_date DATE,
                       cover_type VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE expenses (
                          id UUID PRIMARY KEY,
                          trip_id UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
                          title VARCHAR(255) NOT NULL,
                          amount NUMERIC(15, 2) NOT NULL,
                          currency VARCHAR(10) NOT NULL,
                          category VARCHAR(255) NOT NULL,
                          date DATE NOT NULL,
                          is_average_cost BOOLEAN DEFAULT FALSE,
                          exchange_rate NUMERIC(15, 4),
                          amount_brl NUMERIC(15, 2),
                          photo_path VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE currency_transactions (
                                       id UUID PRIMARY KEY,
                                       user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                       amount NUMERIC(15, 2) NOT NULL,
                                       currency VARCHAR(10) NOT NULL,
                                       amount_brl NUMERIC(15, 2),
                                       source VARCHAR(255),
                                       date DATE NOT NULL,
                                       vet_rate NUMERIC(15, 4),
                                       photo_path VARCHAR(255),
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wallets (
                         user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         currency VARCHAR(10) NOT NULL,
                         balance NUMERIC(15, 2) NOT NULL DEFAULT 0.0,
                         average_vet NUMERIC(15, 4),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (user_id, currency)
);