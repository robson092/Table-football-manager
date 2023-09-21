package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class DataLoader {
    static final Path PATH_TO_TEAMS_FILE = Paths.get("src/Tables/teams_table.json");
    static final Path PATH_TO_USERS_FILE = Paths.get("src/Tables/players_table.json");
    static final Path PATH_TO_GAMES_FILE = Paths.get("src/Tables/games_table.json");
    static final String PATH_TO_TABLES_DIRECTORY = "src/Tables/";

    private DataLoader() {
        throw new UnsupportedOperationException();
    }

    private static Set<String> getDirectoryContent() {
        try (Stream<Path> stream = Files.list(Paths.get(PATH_TO_TABLES_DIRECTORY))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<Map<String, String>> getFileContent(Path path) throws IOException {
        ObjectMapper objectMapper = ObjectMapperProvider.getInstance();
        if (Files.size(path) == 0) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(path.toFile(), new TypeReference<>() {
        });
    }

    static void loadAllPlayersFromFileToDB(List<Map<String, String>> users) {
        for (Map<String, String> singleUser : users) {
            String name = singleUser.get("name");
            Integer teamId = singleUser.get("teamId") == null ? null : Integer.parseInt(singleUser.get("teamId"));
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

    static void loadAllTeamsFromFileToDB(List<Map<String, String>> teams) {
        for (Map<String, String> singleTeam : teams) {
            String name = singleTeam.get("name");
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

    static void loadAllGamesFromFileToDB(List<Map<String, String>> games) {
        for (Map<String, String> singleGame : games) {
            String name = singleGame.get("name");
            String firstTeamId = singleGame.get("firstTeamId");
            String secondTeamId = singleGame.get("secondTeamId");
            String gameTimeString = singleGame.get("gameTime");
            LocalDateTime gameTime = LocalDateTime.parse(gameTimeString);
            String firstTeamGols = singleGame.get("firstTeamGols");
            String secondTeamGols = singleGame.get("secondTeamGols");
            String gameStatus = singleGame.get("gameStatus");
            String result = singleGame.get("result");
            try (Connection connection = DBCPDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("""
                                         INSERT INTO games
                                         (name, team_1_id, team_2_id, game_time, first_team_gols, second_team_gols, game_status, result)
                                         VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )
                         """)) {
                connection.setAutoCommit(false);
                statement.setString(1, name);
                statement.setLong(2, Long.parseLong(firstTeamId));
                statement.setLong(3, Long.parseLong(secondTeamId));
                statement.setTimestamp(4, Timestamp.valueOf(gameTime));
                statement.setInt(5, Integer.parseInt(firstTeamGols));
                statement.setInt(6, Integer.parseInt(secondTeamGols));
                statement.setString(7, gameStatus);
                statement.setString(8, result);
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void loadFilesToDB() throws IOException {
        Set<String> directoryContent = DataLoader.getDirectoryContent();
        for (String fileName : directoryContent) {
            List<Map<String, String>> fileContent = getFileContent(Path.of(PATH_TO_TABLES_DIRECTORY + fileName));
            if (fileName.startsWith("players")) {
                loadAllPlayersFromFileToDB(fileContent);
            }
            if (fileName.startsWith("teams")) {
                loadAllTeamsFromFileToDB(fileContent);
            }
            if (fileName.startsWith("games")) {
                loadAllGamesFromFileToDB(fileContent);
            }
        }
    }

    static boolean checkIfAlreadyExistsInTheFile(String name, Path path) throws IOException {
        List<Map<String, String>> fileContent = DataLoader.getFileContent(path);
        if (fileContent.isEmpty()) {
            return false;
        } else {
            for (Map<String, String> singleName : fileContent) {
                if (singleName.get("name").equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}