package org.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.example.DataLoader.PATH_TO_USERS_FILE;
import static org.example.DataLoader.getFileContent;

public class PlayerService {

    PlayerDao playerDao = new PlayerDao();
    TeamDao teamDao = new TeamDao();
    PlayerRepositoryFile playerRepositoryFile = new PlayerRepositoryFile();

    boolean checkIfPlayerExistsAndHasNoTeam(String playerName) throws IOException {
        List<Map<String, String>> fileContent = getFileContent(PATH_TO_USERS_FILE);
        if (DataLoader.checkIfAlreadyExistsInTheFile(playerName, PATH_TO_USERS_FILE)) {
            for (Map<String, String> singleMapWithUser : fileContent) {
                if (playerName.equals(singleMapWithUser.get("name")) && singleMapWithUser.get("teamId") == null) {
                    return true;
                }
            }
        }
        return false;
    }

    void createPlayer(String name) throws IOException {
        Player player = new Player(name);
        playerRepositoryFile.saveSinglePlayerInTheFile(player);
        playerDao.save(player);
    }

    void addPlayerToTeam(String playerName, String teamName) throws IOException {
        int playerId = playerDao.updatePlayerWithTeamIdInDB(playerName, teamName);
        playerRepositoryFile.saveSinglePlayerInTheFile(new Player(playerName, playerId));
    }

    void removeFromTeam(String name) throws IOException {
        Player player = getPlayerByName(name);
        playerDao.update(player, new String[] {"team_Id", null} );
        player.setTeamId(null);
        playerRepositoryFile.saveSinglePlayerInTheFile(player);
    }

    void delete(String name) throws IOException {
        playerRepositoryFile.deletePlayerFromFile(name);
        playerDao.delete(getPlayerByName(name));
    }

    Player getPlayerByName(String name) {
        List<Player> players = playerDao.getAll();
        return players.stream()
                .filter(player -> player.getName().equals(name))
                .reduce((player, player2) -> player)
                .get();
    }

    Map<String, String> getPlayersWithTeams() {
        List<Player> players = playerDao.getAll();
        List<Team> teams = teamDao.getAll();
        Map<String, String> playersWithTeams = new HashMap<>();
        for (Player player : players) {
            for (Team team : teams) {
                if (player.getTeamId() == team.getId()) {
                    playersWithTeams.put(player.getName(), team.getName());
                }
                if (player.getTeamId() == 0) {
                    playersWithTeams.put(player.getName(), "No Team");
                }
            }
        }
        return playersWithTeams;
    }
}
