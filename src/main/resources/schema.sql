CREATE TABLE IF NOT EXISTS movies (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    movie_year VARCHAR(4),
    imdb_id VARCHAR(30) UNIQUE,
    type VARCHAR(30),
    poster VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS search_history (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    query VARCHAR(30) UNIQUE,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS search_references (
    search_id INTEGER,
    movie_id INTEGER,
    PRIMARY KEY (search_id, movie_id),
    FOREIGN KEY (search_id) REFERENCES search_history(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);