package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

public class DBInitializer {
    private final List<String> SQL_PATH_LIST = List.of("/Data/init/teams.sql", "/Data/init/players.sql", "/Data/init/games.sql");


    void initDB() throws SQLException {
        try (Connection connection = DBCPDataSource.getConnection()) {
            try (var st = connection.createStatement()) {
                for (String path : SQL_PATH_LIST) {
                    initSingleTable(st, path);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    void initSingleTable(Statement statement, String sqlPath) throws SQLException {
        try (var sqlReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(sqlPath)))) {
            statement.execute(sqlReader.lines().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
