DROP TABLE IF EXISTS games CASCADE;
CREATE TABLE games (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    team_1_id BIGINT NOT NULL,
    team_2_id BIGINT NOT NULL,
    game_time TIMESTAMP NOT NULL,
    first_team_gols INT,
    second_team_gols INT,
    game_status VARCHAR(255) NOT NULL,
    result VARCHAR(255),
    CONSTRAINT fk_team_1 FOREIGN KEY (team_1_id) REFERENCES teams (id) ON DELETE CASCADE,
    CONSTRAINT fk_team_2 FOREIGN KEY (team_2_id) REFERENCES teams (id) ON DELETE CASCADE
)