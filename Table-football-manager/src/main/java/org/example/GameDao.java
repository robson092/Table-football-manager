package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameDao implements Dao<Game> {

    TeamDao teamDao = new TeamDao();
    @Override
    public Optional<Game> get(long id) {
        Game game = null;
        String selectSql = "SELECT * FROM games WHERE id = ?";
        try (var connection = DBCPDataSource.getConnection();
             var selectSt = connection.prepareStatement(selectSql)) {
            selectSt.setLong(1, id);
            ResultSet resultSet = selectSt.executeQuery();
            while (resultSet.next()) {
                game = new Game(
                        teamDao.get(resultSet.getInt(3)).get(),
                        teamDao.get(resultSet.getInt(4)).get(),
                        resultSet.getTimestamp(5)
                );
                game.setGameStatus(GameStatus.valueOf(resultSet.getString(7)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(game);
    }

    @Override
    public List<Game> getAll() {
        List<Game> games = new ArrayList<>();
        Game game = null;
        String selectSql = "SELECT * FROM games";
        try (var connection = DBCPDataSource.getConnection();
             var selectSt = connection.prepareStatement(selectSql)) {
            ResultSet resultSet = selectSt.executeQuery();
            while (resultSet.next()) {
                game = new Game(
                        teamDao.get(resultSet.getInt(3)).get(),
                        teamDao.get(resultSet.getInt(4)).get(),
                        resultSet.getTimestamp(5)
                );
                game.setGameStatus(GameStatus.valueOf(resultSet.getString(7)));
                games.add(game);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return games;
    }

    @Override
    public void save(Game game) {
        long firstTeamId = game.getFirstTeam().getId();
        long secondTeamId = game.getSecondTeam().getId();
        String insertSql = """
                INSERT INTO
                games (name, team_1_id, team_2_id, game_time, game_status)
                VALUES
                (?, ?, ?, ?, ?)
                """;
        int gameId = 0;
        try (var connection = DBCPDataSource.getConnection();
             var insertSt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            insertSt.setString(1, game.getName());
            insertSt.setLong(2, firstTeamId);
            insertSt.setLong(3, secondTeamId);
            insertSt.setTimestamp(4, new Timestamp(game.getGameTime().getTime()));
            insertSt.setString(5, game.getGameStatus().toString());
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

    @Override
    public void update(Game game, String[] params) {
        String updateSql = "UPDATE games SET" + params[0] + " = ? WHERE id = ?";
        try (var con = DBCPDataSource.getConnection();
             var updateSt = con.prepareStatement(updateSql)) {
            updateSt.setObject(1, params[1]);
            updateSt.setLong(2, game.getId());
            updateSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Game game) {
        try (var connection = DBCPDataSource.getConnection();
             var deleteSt = connection.prepareStatement("DELETE FROM games WHERE id = ?")) {
            deleteSt.setLong(1, game.getId());
            deleteSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
