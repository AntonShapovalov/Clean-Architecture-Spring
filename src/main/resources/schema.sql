CREATE TABLE movies (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    movie_year VARCHAR(4),
    imdb_id VARCHAR(30) UNIQUE,
    type VARCHAR(30),
    poster VARCHAR(255)
);