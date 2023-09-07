package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class GameRepositoryDB {

    void saveGameToDB(Game game) {
        TeamRepositoryDB teamRepositoryDB = new TeamRepositoryDB();
        String firstTeamName = game.getFirstTeam().getName();
        String secondTeamName = game.getSecondTeam().getName();
        int firstTeamId = teamRepositoryDB.getTeamId(firstTeamName);
        int secondTeamId = teamRepositoryDB.getTeamId(secondTeamName);
        String insertSql = """
                INSERT INTO
                games (team_1_id, team_2_id, game_time, game_status)
                VALUES
                (?, ?, ?, ?)
                """;
        int gameId = 0;
        try (var connection = DBCPDataSource.getConnection();
             var insertSt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            insertSt.setInt(1, firstTeamId);
            insertSt.setInt(2, secondTeamId);
            insertSt.setTimestamp(3, new Timestamp(game.getGameTime().getTime()));
            insertSt.setString(4, game.getGameStatus().toString());
            int rowsAffected = insertSt.executeUpdate();
            connection.commit();
            if (rowsAffected == 1) {
                ResultSet generatedKeys = insertSt.getGeneratedKeys();
                generatedKeys.next();
                gameId = generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
