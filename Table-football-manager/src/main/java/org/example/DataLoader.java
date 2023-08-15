package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DataLoader {
    private final Connection connection;


    public DataLoader(Connection connection) {
        this.connection = connection;
    }

    private List<Map<String, Object>> getFileContent(ObjectMapper objectMapper, Path path) throws IOException {
        return objectMapper.readValue(path.toFile(), new TypeReference<>() {
        });
    }



    void loadUsersToDB() throws IOException {
        List<Map<String, Object>> maps = getFileContent(new ObjectMapper(), Paths.get("users_table.json"));
        for (Map<String, Object> map : maps) {
            int id = (int) map.get("id");
            String name = String.valueOf(map.get("name"));
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users (id, name) VALUES ( ?, ? )")) {
                connection.setAutoCommit(false);
                statement.setInt(1, id);
                statement.setString(2, name);
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
