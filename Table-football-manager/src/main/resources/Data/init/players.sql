DROP TABLE IF EXISTS players CASCADE;
CREATE TABLE players (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    team_id BIGINT,
    gols INT,
    CONSTRAINT fk_players FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE SET NULL
)