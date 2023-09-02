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
    private static final PlayerRepositoryDB playerRepositoryDB = new PlayerRepositoryDB();
    private static final TeamRepositoryDB teamRepositoryDB = new TeamRepositoryDB();


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

    static void loadFilesToDB() throws IOException, SQLException {
        Set<String> directoryContent = DataLoader.getDirectoryContent();
        for (String fileName : directoryContent) {
            List<Map<String, Object>> fileContent = getFileContent(Path.of(PATH_TO_TABLES_DIRECTORY + fileName));
            if (fileName.startsWith("players")) {
                playerRepositoryDB.loadAllPlayersFromFileToDB(fileContent);
            }
            if (fileName.startsWith("teams")) {
                teamRepositoryDB.loadAllTeamsFromFileToDB(fileContent);
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