package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamRepositoryDB {

    void loadAllTeamsFromFileToDB(List<Map<String, Object>> teams) throws SQLException {
        for (Map<String, Object> singleTeam : teams) {
            String name = (String) singleTeam.get("name");
            try (Connection connection = DBCPDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO teams (name) VALUES ( ? )")) {
                connection.setAutoCommit(false);
                statement.setString(1, name);
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void saveSingleTeamToDB(Team team) throws SQLException {
        String sql = "INSERT INTO teams (name) VALUES ( ? )";
        int teamId = 0;
        try (var connection = DBCPDataSource.getConnection();
             var st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            st.setString(1, team.getName());
            int rowsAffected = st.executeUpdate();
            connection.commit();
            if (rowsAffected == 1) {
                ResultSet generatedKeys = st.getGeneratedKeys();
                generatedKeys.next();
                teamId = generatedKeys.getInt(1);
            }
            //team.setId(teamId);
        }
    }

    List<Map<String, String>> getAllTeams() {
        List<Map<String, String>> listOfTeams = new ArrayList<>();
        String selectSql = "SELECT * FROM teams";
        try (var connection = DBCPDataSource.getConnection();
             var selectSt = connection.prepareStatement(selectSql)) {
            ResultSet resultSetWithTeams = selectSt.executeQuery();
            while (resultSetWithTeams.next()) {
                Map<String, String> mapWithTeams = new HashMap<>();
                mapWithTeams.put("id", String.valueOf(resultSetWithTeams.getInt(1)));
                mapWithTeams.put("team", resultSetWithTeams.getString(2));
                listOfTeams.add(mapWithTeams);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listOfTeams;
    }

    int getTeamId(String name) {
        int id = 0;
        String selectSql = "SELECT * FROM teams WHERE name = ?";
        try (var connection = DBCPDataSource.getConnection();
            var selectSt = connection.prepareStatement(selectSql)) {
            selectSt.setString(1, name);
            ResultSet resultSet = selectSt.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }
}
