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

    Set<String> getDirectoryContent() {
        try (Stream<Path> stream = Files.list(Paths.get("src/Tables"))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<Map<String, String>> getFileContent(Path path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (Files.size(path) == 0) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(path.toFile(), new TypeReference<>() {
        });
    }

    static void loadFilesToDB() throws IOException, SQLException {
        String directoryPath = "src/Tables/";
        Set<String> directoryContent = new DataLoader().getDirectoryContent();
        for (String fileName : directoryContent) {
            List<Map<String, String>> fileContent = getFileContent(Path.of(directoryPath + fileName));
            if (fileName.startsWith("users")) {
                loadUsersToDB(fileContent);
            }
        }
    }

    static void loadUsersToDB(List<Map<String, String>> users) throws SQLException {
        for (Map<String, String> singleUser : users) {
            String name = singleUser.get("name");
            String team = singleUser.get("team");
            try (Connection connection = DBCPDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO users (name, team) VALUES ( ?, ? )")) {
                connection.setAutoCommit(false);
                statement.setString(1, name);
                statement.setString(2, team);
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void saveUserInTheFile(User user) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Path path = Paths.get("src/Tables/users_table.json");
        if (Files.size(path) != 0) {
            List<Map<String, String>> fileContent = getFileContent(path);
            Map<String, String> newUser = new HashMap<>();
            newUser.put("name", user.getName());
            newUser.put("team", user.getTeam());
            fileContent.add(newUser);
            objectMapper.writeValue(path.toFile(), fileContent);
        } else {
            Map<String, String> newUser = new HashMap<>();
            newUser.put("name", user.getName());
            newUser.put("team", user.getTeam());
            List<Map<String, String>> listOfUsers = new ArrayList<>();
            listOfUsers.add(newUser);
            objectMapper.writeValue(path.toFile(), listOfUsers);
        }
    }

    static void saveSingleUserToDB(User user) throws SQLException {
        String sql = "INSERT INTO users (name, team) VALUES ( ?, ? )";
        Integer userId = null;
        try (var connection = DBCPDataSource.getConnection();
             var st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            st.setString(1, user.getName());
            st.setString(2, user.getTeam());
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
}