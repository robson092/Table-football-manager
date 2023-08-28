package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataLoader {
    static final Path PATH_TO_TEAMS_FILE = Paths.get("src/Tables/teams_table.json");
    static final Path PATH_TO_USERS_FILE = Paths.get("src/Tables/players_table.json");
    static final String PATH_TO_TABLES_DIRECTORY = "src/Tables/";

    Set<String> getDirectoryContent() {
        try (Stream<Path> stream = Files.list(Paths.get(PATH_TO_TABLES_DIRECTORY))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<Map<String, Object>> getFileContent(Path path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (Files.size(path) == 0) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(path.toFile(), new TypeReference<>() {
        });
    }

    static void loadFilesToDB() throws IOException, SQLException {
        Set<String> directoryContent = new DataLoader().getDirectoryContent();
        for (String fileName : directoryContent) {
            List<Map<String, Object>> fileContent = getFileContent(Path.of(PATH_TO_TABLES_DIRECTORY + fileName));
            if (fileName.startsWith("players")) {
                loadAllPlayersFromFileToDB(fileContent);
            }
            if (fileName.startsWith("teams")) {
                loadAllTeamsFromFileToDB(fileContent);
            }
        }
    }

    static void loadAllPlayersFromFileToDB(List<Map<String, Object>> users) throws SQLException {
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
    static void loadAllTeamsFromFileToDB(List<Map<String, Object>> teams) throws SQLException {
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

    static void saveSinglePlayerInTheFile(Player player) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> newUser = new HashMap<>();
        if (Files.size(PATH_TO_USERS_FILE) != 0) {
            List<Map<String, Object>> fileContent = getFileContent(PATH_TO_USERS_FILE);
            for (Map<String, Object> singleMapWithUser : fileContent) {
                if (singleMapWithUser.get("name").equals(player.getName())) {
                    singleMapWithUser.replace("name", player.getName());
                    singleMapWithUser.put("teamId", player.getTeamId());
                    objectMapper.writeValue(PATH_TO_USERS_FILE.toFile(), fileContent);
                    return;
                }
            }
            newUser.put("name", player.getName());
            newUser.put("teamId", player.getTeamId());
            fileContent.add(newUser);
            objectMapper.writeValue(PATH_TO_USERS_FILE.toFile(), fileContent);

        } else {
            newUser.put("name", player.getName());
            newUser.put("teamId", player.getTeamId());
            List<Map<String, Object>> listOfUsers = new ArrayList<>();
            listOfUsers.add(newUser);
            objectMapper.writeValue(PATH_TO_USERS_FILE.toFile(), listOfUsers);
        }
    }

    static void saveSinglePlayerToDB(Player player) throws SQLException {
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

    static void saveSingleTeamInTheFile(Team team) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (Files.size(PATH_TO_TEAMS_FILE) != 0) {
            List<Map<String, Object>> listOfTeams = getFileContent(PATH_TO_TEAMS_FILE);
            Map<String, Object> newTeam = new HashMap<>();
            newTeam.put("name", team.getName());
            listOfTeams.add(newTeam);
            objectMapper.writeValue(PATH_TO_TEAMS_FILE.toFile(), listOfTeams);
        } else {
            Map<String, String> newTeam = new HashMap<>();
            newTeam.put("name", team.getName());
            List<Map<String, String>> listOfTeams = new ArrayList<>();
            listOfTeams.add(newTeam);
            objectMapper.writeValue(PATH_TO_TEAMS_FILE.toFile(), listOfTeams);
        }
    }

    static void saveSingleTeamToDB(Team team) throws SQLException {
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


    static void updatePlayerWithTeamIdInFileAndDB(String playerName, String teamName) throws IOException {
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
        saveSinglePlayerInTheFile(new Player(playerName, id));
    }

    static boolean checkIfPlayerExistsAndHasTeam(String playerName) throws IOException {
        List<Map<String, Object>> fileContent = getFileContent(PATH_TO_USERS_FILE);
        if (Main.checkIfAlreadyExistsInTheFile(playerName, PATH_TO_USERS_FILE)) {
            for (Map<String, Object> singleMapWithUser : fileContent) {
                if (playerName.equals(singleMapWithUser.get("name")) && !Objects.equals(singleMapWithUser.get("teamId"),null)) {
                    return true;
                }
            }
        }
        return false;
    }

    static void editTeamForGivenPlayer(String playerName) throws IOException, SQLException {
        Player player = new Player(playerName, null);
        saveSinglePlayerInTheFile(player);
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

    static void deletePlayerFromFile(String playerName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> fileContent = getFileContent(PATH_TO_USERS_FILE);
        for (Map<String, Object> singleMapWithUser : fileContent) {
            if (singleMapWithUser.get("name").equals(playerName)) {
                singleMapWithUser.remove("name");
                singleMapWithUser.remove("teamId");
            }
        }
        fileContent.removeIf(Map::isEmpty);
        objectMapper.writeValue(PATH_TO_USERS_FILE.toFile(), fileContent);
    }

    static void deletePlayerFromDB(String playerName) {
        int id = 0;
        String selectSql = "SELECT id FROM players WHERE name = ?";
        String deleteSql = "DELETE FROM players WHERE id = ?";
        try (var connection = DBCPDataSource.getConnection();
            var selectSt = connection.prepareStatement(selectSql);
            var deleteSt = connection.prepareStatement(deleteSql)) {
            selectSt.setString(1, playerName);
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
}