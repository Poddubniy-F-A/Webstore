CREATE TABLE IF NOT EXISTS users (
    login VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    nick VARCHAR(255),
    role ENUM('CUST', 'MOD', 'WW') NOT NULL
);

CREATE TABLE IF NOT EXISTS goods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    description TEXT,
    picture_path VARCHAR(255),
    category VARCHAR(255),
    brand VARCHAR(255),
    price INTEGER NOT NULL CHECK (price > 0),
    count INTEGER DEFAULT 0 CHECK (count >= 0),
    orders_count INTEGER DEFAULT 0 CHECK (orders_count >= 0),
    ratings_sum INTEGER DEFAULT 0 CHECK (ratings_sum >= 0),
    ratings_count INTEGER DEFAULT 0 CHECK (ratings_count >= 0)
);

INSERT INTO users (login, password, nick, role) VALUES
('user1', 'pass1', 'alexa', 'CUST'),
('user2', 'pass2', 'bobik', 'CUST'),
('user3', 'pass3', 'carlan', 'CUST'),
('Alex111', '123', 'Alexander', 'MOD'),
('Bob222', '345', 'Robert', 'WW'),
('Carl333', '123', 'Carl', 'MOD');

INSERT INTO goods (label, description, picture_path, category, brand, price, count) VALUES
('Good1', 'desc1', 'https://drive.google.com/thumbnail?id=1AbAdy5HysX1TcSnJ9fQlXE7tCqVr-o8M', 'cat1', 'brand1', 100, 1),
('Good2', 'desc2', 'https://drive.google.com/thumbnail?id=1uJAL22UIEBYlyjqMlEELU18ifj3EGnPN', 'cat1', 'brand2', 200, 1),
('Good3', 'desc3', 'https://drive.google.com/thumbnail?id=1JgYOel7t1fIeZZEjJCUka8pMZB26vwrF', 'cat2', 'brand3', 50, 1),
('Good4', 'desc4', 'https://drive.google.com/thumbnail?id=1qiHigY-eVjX5mb07OJG5hB1amBlZtlSo', 'cat2', 'brand3', 250, 1),
('Good5', 'desc5', 'https://drive.google.com/thumbnail?id=1fiRH0Fq_6JVg6Phm4hkeeMh5HusSzvk4', 'cat3', 'brand4', 10, 1);