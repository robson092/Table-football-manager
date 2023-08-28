package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    private static void createNewPlayer() throws IOException, SQLException {
        sc.nextLine();
        System.out.println("Provide player's name:");
        String playerName = sc.nextLine();
        while (checkIfAlreadyExistsInTheFile(playerName, DataLoader.PATH_TO_USERS_FILE)) {
            System.out.println("Player is already exists. Please provide another name.");
            playerName = sc.nextLine();
        }
        Player player = new Player(playerName);
        DataLoader.saveSinglePlayerInTheFile(player);
        DataLoader.saveSinglePlayerToDB(player);
        System.out.println("Player has been successfully created!");
    }

    private static void createNewTeam() throws SQLException, IOException {
        sc.nextLine();
        System.out.println("Provide team's name:");
        String teamName = sc.nextLine();
        while (checkIfAlreadyExistsInTheFile(teamName, DataLoader.PATH_TO_TEAMS_FILE)) {
            System.out.println("Team is already exists. Please provide another name.");
            teamName = sc.nextLine();
        }
        Team team = new Team(teamName);
        DataLoader.saveSingleTeamInTheFile(team);
        DataLoader.saveSingleTeamToDB(team);
        System.out.println("Player has been successfully created!");
        backToMenu(teamName);
    }

    private static void addPlayerToTeam() throws IOException {
        sc.nextLine();
        System.out.println("Provide team's name:");
        String teamName = sc.nextLine();
        while (!checkIfAlreadyExistsInTheFile(teamName, DataLoader.PATH_TO_TEAMS_FILE)) {
            System.out.println("There is no such team. Please provide correct team's name.");
            teamName = sc.nextLine();
        }
        System.out.println("Provide player's name:");
        String playerName = sc.nextLine();
        while (DataLoader.checkIfPlayerExistsAndHasTeam(playerName)) {
            System.out.println("Provided player either does not exist or already has a team. Please provide another player.");
            playerName = sc.nextLine();
        }
        DataLoader.updatePlayerWithTeamIdInFileAndDB(playerName, teamName);
    }

    private static void choosePlayerToRemoveFromTeam() throws IOException, SQLException {
        sc.nextLine();
        System.out.println("Please provide player name to remove from team.");
        String playerName = sc.nextLine();
        while (!DataLoader.checkIfPlayerExistsAndHasTeam(playerName)) {
            System.out.println("Provided player either does not exists or does not has a team. Please provide another player.");
            playerName = sc.nextLine();
        }
        DataLoader.editTeamForGivenPlayer(playerName);
    }

    private static void deletePlayer() throws IOException {
        sc.nextLine();
        System.out.println("Please provide player name to delete from application.");
        String playerName = sc.nextLine();
        while (!checkIfAlreadyExistsInTheFile(playerName, DataLoader.PATH_TO_USERS_FILE)) {
            System.out.println("Provided player does not exists. Please provide another player.");
            playerName = sc.nextLine();
        }
        DataLoader.deletePlayerFromFile(playerName);
        DataLoader.deletePlayerFromDB(playerName);
    }

    private static void getGameTimeAndTeams() {
        sc.nextLine();
        String patter = "\\d{2}-\\d{2}";
        System.out.println("Provide game time (dd-mm):");
        while (!sc.hasNext(Pattern.compile(patter))) {
            System.out.println("Incorrect game time format!");
            sc.next();
        }
        String gameTime = sc.nextLine();
        System.out.println("Choose first team");
        String firstTeam = sc.nextLine();
        System.out.println("Choose second team");
        String secondTeam = sc.nextLine();

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

    private static void getMenu() throws SQLException, IOException {
        System.out.println("Available actions:");
        System.out.println("""
                1. Create player's account
                2. Create team
                3. Add player to team
                4. Remove player from team
                5. Delete player
                6. Delete team
                7. Plan game
                8. Edit game
                9. Show all teams
                10. Show all players
                11. Show game scheduler
                """);
        System.out.println("Choose one of above number!");
        while (!sc.hasNextInt()) {
            System.out.println("That's not a number!");
            sc.next();
        }
        int userChoice = sc.nextInt();
        switch (userChoice) {
            case 1 -> createNewPlayer();
            case 2 -> createNewTeam();
            case 3 -> addPlayerToTeam();
            case 4 -> choosePlayerToRemoveFromTeam();
            case 5 -> deletePlayer();
            case 6 -> System.out.println("User chose to delete team");
            case 7 -> getGameTimeAndTeams();
            case 8 -> System.out.println("User chose to edit game");
            case 9 -> System.out.println("User chose to show all teams");
            case 10 -> System.out.println("User chose to show all players");
            case 11 -> System.out.println("User chose to show all scheduled games");
            default -> System.out.println("Incorrect number chosen! Please try again!");
        }
    }

    private static void backToMenu(String input) throws SQLException, IOException {
        if (input.equalsIgnoreCase("back")) {
            getMenu();
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        System.out.println("Welcome to Table football manager!");
        new DBInitializer().initDB();
        DataLoader.loadFilesToDB();
        while (true) {
            getMenu();
        }
    }
}