package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.example.DataLoader.PATH_TO_USERS_FILE;
import static org.example.DataLoader.getFileContent;

public class PlayerService {

    PlayerDao playerDao = new PlayerDao();

    boolean checkIfPlayerExistsAndHasNoTeam(String playerName) throws IOException {
        List<Map<String, Object>> fileContent = getFileContent(PATH_TO_USERS_FILE);
        if (DataLoader.checkIfAlreadyExistsInTheFile(playerName, PATH_TO_USERS_FILE)) {
            for (Map<String, Object> singleMapWithUser : fileContent) {
                if (playerName.equals(singleMapWithUser.get("name")) && Objects.equals(singleMapWithUser.get("teamId"),null)) {
                    return true;
                }
            }
        }
        return false;
    }

    Player getPlayerByName(String name) {
        List<Player> players = playerDao.getAll();
        return players.stream()
                .filter(player -> player.getName().equals(name))
                .reduce((player, player2) -> player)
                .get();
    }
}
