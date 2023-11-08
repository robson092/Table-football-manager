package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.DataLoader.PATH_TO_USERS_FILE;
import static org.example.DataLoader.getFileContent;

public class PlayerRepositoryFile {

    boolean checkIfPlayerTeamIsNull(Player player) {
        return player.getTeamId() == null;
    }

    void saveSinglePlayerInTheFile(Player player) throws IOException {
        ObjectMapper objectMapper = ObjectMapperProvider.getInstance();
        Map<String, String> newUser = new HashMap<>();
        if (Files.size(PATH_TO_USERS_FILE) != 0) {
            List<Map<String, String>> fileContent = getFileContent(PATH_TO_USERS_FILE);
            for (Map<String, String> singleMapWithUser : fileContent) {
                if (singleMapWithUser.get("name").equals(player.getName())) {
                    singleMapWithUser.replace("name", player.getName());
                    singleMapWithUser.put("teamId", checkIfPlayerTeamIsNull(player) ? null : String.valueOf(player.getTeamId()));
                    objectMapper.writeValue(PATH_TO_USERS_FILE.toFile(), fileContent);
                    return;
                }
            }
            newUser.put("name", player.getName());
            newUser.put("teamId", checkIfPlayerTeamIsNull(player) ? null : String.valueOf(player.getTeamId()));
            fileContent.add(newUser);
            objectMapper.writeValue(PATH_TO_USERS_FILE.toFile(), fileContent);

        } else {
            newUser.put("name", player.getName());
            newUser.put("teamId", checkIfPlayerTeamIsNull(player) ? null : String.valueOf(player.getTeamId()));
            List<Map<String, String>> listOfUsers = new ArrayList<>();
            listOfUsers.add(newUser);
            objectMapper.writeValue(PATH_TO_USERS_FILE.toFile(), listOfUsers);
        }
    }

    void deletePlayerFromFile(String playerName) throws IOException {
        ObjectMapper objectMapper = ObjectMapperProvider.getInstance();
        List<Map<String, String>> fileContent = getFileContent(PATH_TO_USERS_FILE);
        for (Map<String, String> singleMapWithUser : fileContent) {
            if (singleMapWithUser.get("name").equalsIgnoreCase(playerName)) {
                singleMapWithUser.remove("name");
                singleMapWithUser.remove("teamId");
            }
        }
        fileContent.removeIf(Map::isEmpty);
        objectMapper.writeValue(PATH_TO_USERS_FILE.toFile(), fileContent);
    }

    void updateAllPlayersInFileWhichTeamHasBeenDelete(String teamName) {
        int id = 0;
        List<String> playersName = new ArrayList<>();
        try (var connection = DBCPDataSource.getConnection();
             var selectTeamSt = connection.prepareStatement("SELECT id FROM teams WHERE name = ?");
             var selectPlayersSt = connection.prepareStatement("SELECT * FROM players WHERE team_id = ?")) {
            selectTeamSt.setString(1, teamName);
            ResultSet teamWithGivenId = selectTeamSt.executeQuery();
            while (teamWithGivenId.next()) {
                id = teamWithGivenId.getInt(1);
            }
            selectPlayersSt.setInt(1, id);
            ResultSet allPlayersWithGivenId = selectPlayersSt.executeQuery();
            while (allPlayersWithGivenId.next()) {
                playersName.add(allPlayersWithGivenId.getString(2));
            }
            for (String name : playersName) {
                saveSinglePlayerInTheFile(new Player(name));
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
