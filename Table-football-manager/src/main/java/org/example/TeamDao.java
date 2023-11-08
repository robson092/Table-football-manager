package org.example;

import java.sql.*;
import java.util.*;

public class TeamDao implements Dao<Team> {

    @Override
    public Optional<Team> get(long id) {
        Team team = null;
        String selectSql = "SELECT * FROM teams WHERE id = ?";
        try (var connection = DBCPDataSource.getConnection();
             var selectSt = connection.prepareStatement(selectSql)) {
            selectSt.setLong(1, id);
            ResultSet resultSet = selectSt.executeQuery();
            while (resultSet.next()) {
                team = new Team(
                        resultSet.getInt(1),
                        resultSet.getString(2)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(team);
    }

    @Override
    public List<Team> getAll() {
        List<Team> teams = new ArrayList<>();
        Team team = null;
        String selectSql = "SELECT * FROM teams";
        try (var connection = DBCPDataSource.getConnection();
             var selectSt = connection.prepareStatement(selectSql)) {
            ResultSet resultSetWithTeams = selectSt.executeQuery();
            while (resultSetWithTeams.next()) {
                team = new Team(
                        resultSetWithTeams.getInt(1),
                        resultSetWithTeams.getString(2)
                );
                teams.add(team);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teams;
    }

    @Override
    public long save(Team team) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teamId;
    }

    @Override
    public void update(Team team, String[] params) {
        String updateSql = "UPDATE teams SET" + params[0] + " = ? WHERE name = ?";
        try (var con = DBCPDataSource.getConnection();
             var updateSt = con.prepareStatement(updateSql)) {
            updateSt.setObject(1, params[1]);
            updateSt.setString(2, team.getName());
            updateSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Team team) {
        long id = 0;
        try (var connection = DBCPDataSource.getConnection();
             var selectSt = connection.prepareStatement("SELECT id FROM teams WHERE name = ?");
             var deleteSt = connection.prepareStatement("DELETE FROM teams WHERE id = ?")) {
            selectSt.setString(1, team.getName());
            ResultSet resultSet = selectSt.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            deleteSt.setLong(1, id);
            deleteSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
