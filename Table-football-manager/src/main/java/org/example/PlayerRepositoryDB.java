package org.example;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerRepositoryDB {

    void loadAllPlayersFromFileToDB(List<Map<String, Object>> users) throws SQLException {
        for (Map<String, Object> singleUser : users) {
            String name = (String) singleUser.get("name");
            Integer teamId = (Integer) singleUser.get("teamId");
            try (Connection connection = DBCPDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO players (name, team_id) VALUES ( ?, ? )")) {
                connection.setAutoCommit(false);
                statement.setString(1, name);
                statement.setObject(2, teamId);
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void saveSinglePlayerToDB(Player player) throws SQLException {
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
        }
    }

    void editTeamForGivenPlayer(String playerName) throws IOException {
        PlayerRepositoryFile playerRepositoryFile = new PlayerRepositoryFile();
        Player player = new Player(playerName, null);
        playerRepositoryFile.saveSinglePlayerInTheFile(player);
        String updateSql = "UPDATE players SET team_id = ? WHERE name = ?";
        try (var con = DBCPDataSource.getConnection();
             var updateSt = con.prepareStatement(updateSql)) {
            updateSt.setObject(1, player.getTeamId());
            updateSt.setString(2, playerName);
            updateSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteFromDB(String table, String name) {
        int id = 0;
        try (var connection = DBCPDataSource.getConnection();
             var selectSt = connection.prepareStatement("SELECT id FROM " + table + " WHERE name = ?");
             var deleteSt = connection.prepareStatement("DELETE FROM " + table + " WHERE id = ?")) {
            selectSt.setString(1, name);
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

    List<Map<String, String>> getAllPlayersWithTeamNames() {
        List<Map<String, String>> listOfPlayersWithTeams = new ArrayList<>();
        Map<Integer, String> teamsWithId = new HashMap<>();
        String selectPlayersSql = "SELECT * FROM players";
        String selectTeamByIdSql = "SELECT * FROM teams";
        try (var connection = DBCPDataSource.getConnection();
             var selectPlayersSt = connection.prepareStatement(selectPlayersSql);
             var selectTeamByIdSt = connection.prepareStatement(selectTeamByIdSql)) {
            ResultSet resultSetWithTeams = selectTeamByIdSt.executeQuery();
            while (resultSetWithTeams.next()) {
                teamsWithId.put(resultSetWithTeams.getInt(1), resultSetWithTeams.getString(2));
            }
            ResultSet resultSetWithPlayers = selectPlayersSt.executeQuery();
            while (resultSetWithPlayers.next()) {
                Map<String, String> mapWithPlayers = new HashMap<>();
                mapWithPlayers.put("name", resultSetWithPlayers.getString(2));
                for (Map.Entry<Integer, String> entry : teamsWithId.entrySet()) {
                    if (resultSetWithPlayers.getInt(3) == entry.getKey()) {
                        mapWithPlayers.put("team", entry.getValue());
                    }
                }
                listOfPlayersWithTeams.add(mapWithPlayers);
            }
            for (Map<String, String> mapWithPlayers : listOfPlayersWithTeams) {
                if (!mapWithPlayers.containsKey("team")) {
                    mapWithPlayers.put("team", "no team");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listOfPlayersWithTeams;
    }

    int updatePlayerWithTeamIdInDB(String playerName, String teamName) throws IOException {
        PlayerRepositoryFile playerRepositoryFile = new PlayerRepositoryFile();
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
