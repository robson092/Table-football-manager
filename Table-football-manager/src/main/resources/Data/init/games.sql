DROP TABLE IF EXISTS games CASCADE;
CREATE TABLE games (
    id SERIAL PRIMARY KEY,
    team_1_id INT NOT NULL UNIQUE,
    team_2_id INT NOT NULL UNIQUE,
    game_time TIMESTAMP NOT NULL,
    first_team_gols INT,
    second_team_gols INT,
    game_status VARCHAR(255) NOT NULL,
    result VARCHAR(255) NOT NULL,
    CONSTRAINT fk_team_1 FOREIGN KEY (team_1_id) REFERENCES teams (id) ON DELETE CASCADE,
    CONSTRAINT fk_team_2 FOREIGN KEY (team_2_id) REFERENCES teams (id) ON DELETE CASCADE
)