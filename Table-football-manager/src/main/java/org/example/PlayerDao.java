package org.example;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class PlayerDao implements Dao<Player> {

    @Override
    public Optional<Player> get(long id) {
        Player player = null;
        String selectSql = "SELECT * FROM players WHERE id = ?";
        try (var connection = DBCPDataSource.getConnection();
             var selectSt = connection.prepareStatement(selectSql)) {
            selectSt.setLong(1, id);
            ResultSet resultSet = selectSt.executeQuery();
            while (resultSet.next()) {
                player = new Player(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(player);
    }

    @Override
    public List<Player> getAll() {
        Player player = null;
        List<Player> listOfPlayersWithTeams = new ArrayList<>();
        String selectPlayersSql = "SELECT * FROM players";
        try (var connection = DBCPDataSource.getConnection();
             var selectPlayersSt = connection.prepareStatement(selectPlayersSql)) {
            ResultSet resultSetWithPlayers = selectPlayersSt.executeQuery();
            while (resultSetWithPlayers.next()) {
                player = new Player(
                        resultSetWithPlayers.getInt(1),
                        resultSetWithPlayers.getString(2),
                        resultSetWithPlayers.getInt(3)
                );
                listOfPlayersWithTeams.add(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listOfPlayersWithTeams;
    }

    @Override
    public long save(Player player) {
        String sql = "INSERT INTO players (name, team_id) VALUES ( ?, ?)";
        int userId = 0;
        try (var connection = DBCPDataSource.getConnection();
             var st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            st.setString(1, player.getName());
            st.setObject(2, player.getTeamId());
            int rowsAffected = st.executeUpdate();
            connection.commit();
            if (rowsAffected == 1) {
                ResultSet generatedKeys = st.getGeneratedKeys();
                generatedKeys.next();
                userId = generatedKeys.getInt(1);
            }
            //user.setId(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userId;
    }

    @Override
    public void update(Player player, String[] params) {
        String updateSql = "UPDATE players SET " + params[0] + " = ? WHERE name = ?";
        try (var con = DBCPDataSource.getConnection();
             var updateSt = con.prepareStatement(updateSql)) {
            updateSt.setObject(1, params[1]);
            updateSt.setString(2, player.getName());
            updateSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Player player) {
        int id = 0;
        try (var connection = DBCPDataSource.getConnection();
             var selectSt = connection.prepareStatement("SELECT id FROM players WHERE name = ?");
             var deleteSt = connection.prepareStatement("DELETE FROM players WHERE id = ?")) {
            selectSt.setString(1, player.getName());
            ResultSet resultSet = selectSt.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            deleteSt.setInt(1, id);
            deleteSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    int updatePlayerWithTeamIdInDB(String playerName, String teamName) {
        int id = 0;
        String selectSql = "SELECT id FROM teams WHERE name = ?";
        String updateSql = "UPDATE players SET team_id = ? WHERE name = ?";
        try (var con = DBCPDataSource.getConnection();
             var selectSt = con.prepareStatement(selectSql);
             var updateSt = con.prepareStatement(updateSql)) {
            selectSt.setString(1, teamName);
            ResultSet resultSet = selectSt.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            updateSt.setInt(1, id);
            updateSt.setString(2, playerName);
            updateSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }
}
