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
    private static final PlayerDao PLAYER_DAO = new PlayerDao();
    private static final TeamDao TEAM_DAO = new TeamDao();


    private static Set<String> getDirectoryContent() {
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

    static void loadFilesToDB() throws IOException, SQLException {
        Set<String> directoryContent = DataLoader.getDirectoryContent();
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

    static boolean checkIfAlreadyExistsInTheFile(String name, Path path) throws IOException {
        List<Map<String, Object>> fileContent = DataLoader.getFileContent(path);
        if (fileContent.isEmpty()) {
            return false;
        } else {
            for (Map<String, Object> singleName : fileContent) {
                if (singleName.get("name").equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}