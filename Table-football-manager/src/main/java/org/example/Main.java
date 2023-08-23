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
        Path pathToPlayersFile = Path.of("src/Tables/users_table.json");
        while (checkIfAlreadyExistsInTheFile(playerName, pathToPlayersFile)) {
            System.out.println("Player is already exists. Please provide another name.");
            playerName = sc.nextLine();
        }
        User user = new User(playerName);
        DataLoader.saveSingleUserInTheFile(user);
        DataLoader.saveSingleUserToDB(user);
        System.out.println("Player has been successfully created!");
    }

    private static void createNewTeam() throws SQLException, IOException {
        sc.nextLine();
        System.out.println("Provide team's name:");
        String teamName = sc.nextLine();
        Path pathToTeamsFile = Path.of("src/Tables/teams_table.json");
        while (checkIfAlreadyExistsInTheFile(teamName, pathToTeamsFile)) {
            System.out.println("Team is already exists. Please provide another name.");
            teamName = sc.nextLine();
        }
        Team team = new Team(teamName);
        DataLoader.saveSingleTeamInTheFile(team);
        DataLoader.saveSingleTeamToDB(team);
        System.out.println("Player has been successfully created!");
        backToMenu(teamName);
    }

    private static void getTeamNameAndPlayerName() {
        sc.nextLine();
        System.out.println("Provide team's name:");
        String teamName = sc.nextLine();
        System.out.println("Provide player's name:");
        String playerName = sc.nextLine();
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

    private static boolean checkIfAlreadyExistsInTheFile(String name, Path path) throws IOException {
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

    private static void getMenu() throws SQLException, IOException {
        System.out.println("Available actions:");
        System.out.println("""
                1. Create player's account
                2. Create team
                3. Add player to team
                4. Remove player from team
                5. Move player to another team
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
            case 3 -> getTeamNameAndPlayerName();
            case 4 -> System.out.println("User chose to remove player from team");
            case 5 -> System.out.println("User chose to move player to another team");
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