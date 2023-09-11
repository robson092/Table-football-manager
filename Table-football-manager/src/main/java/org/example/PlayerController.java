package org.example;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import static org.example.DataLoader.checkIfAlreadyExistsInTheFile;

public class PlayerController {
    private static final Scanner sc = new Scanner(System.in);
    PlayerService playerService = new PlayerService();
    TeamService teamService = new TeamService();

    void savePlayer(String name) throws IOException {
        while (checkIfAlreadyExistsInTheFile(name, DataLoader.PATH_TO_USERS_FILE)) {
            System.out.println("Player is already exists. Please provide another name.");
            name = sc.nextLine();
        }
        playerService.createPlayer(name);
    }

    void savePlayerInTeam(String playerName, String teamName) throws IOException {
        while (!playerService.checkIfPlayerExistsAndHasNoTeam(playerName)) {
            System.out.println("Provided player either does not exist or already has a team. Please provide another player.");
            playerName = sc.nextLine();
        }
        playerService.addPlayerToTeam(playerName, teamName);
    }

    void removePlayerFromTeam(String name) throws IOException {
        while (playerService.checkIfPlayerExistsAndHasNoTeam(name)) {
            System.out.println("Provided player either does not exists or does not has a team. Please provide another player.");
            name = sc.nextLine();
        }
        playerService.removeFromTeam(name);
    }

    void deletePlayer(String name) throws IOException {
        while (!checkIfAlreadyExistsInTheFile(name, DataLoader.PATH_TO_USERS_FILE)) {
            System.out.println("Provided player does not exists. Please provide another player.");
            name = sc.nextLine();
        }
        playerService.delete(name);
    }

    void showPlayersWithTeams() {
        Map<String, String> playersWithTeams = playerService.getPlayersWithTeams();
        for (Map.Entry<String, String> entry : playersWithTeams.entrySet()) {
            System.out.println("Player:" + entry.getKey() + "   Team: " + entry.getValue());
        }
    }
}
