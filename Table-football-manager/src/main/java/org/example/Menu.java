package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Menu {
    private static final Scanner sc = new Scanner(System.in);
    private static final PlayerController playerController = new PlayerController();
    private static final TeamController teamController = new TeamController();
    private static final GameController gameController = new GameController();

    private void createNewPlayerChoice() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Provide player's name:");
        String playerName = sc.nextLine();
        playerController.savePlayer(playerName);
        System.out.println("Player has been successfully created!");
        Thread.sleep(2000);
    }

    private void createNewTeamChoice() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Provide team's name:");
        String teamName = sc.nextLine();
        teamController.createTeam(teamName);
        System.out.println("Team has been successfully created!");
        Thread.sleep(2000);
    }

    private void addPlayerToTeamChoice() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Provide team's name:");
        String teamName = sc.nextLine();
        String validatedTeam = teamController.validateTeam(teamName);
        System.out.println("Provide player's name:");
        String playerName = sc.nextLine();
        playerController.savePlayerInTeam(playerName, validatedTeam);
        System.out.println("Player has been successfully added to the team.");
        Thread.sleep(2000);
    }

    private void removePlayerFromTeamChoice() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Please provide player name to remove from team.");
        String playerName = sc.nextLine();
        playerController.removePlayerFromTeam(playerName);
        System.out.println("Player has been successfully removed from the team.");
        Thread.sleep(2000);
    }

    private void deletePlayerChoice() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Please provide player name to delete from application.");
        String playerName = sc.nextLine();
        playerController.deletePlayer(playerName);
        System.out.println("Player has been successfully deleted from application.");
        Thread.sleep(2000);
    }

    private void deleteTeamChoice() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Please provide team name to delete from application.");
        String teamName = sc.nextLine();
        teamController.deleteTeam(teamName);
        System.out.println("Team has been successfully deleted from application.");
        Thread.sleep(2000);
    }

    private void registerGameChoice() throws IOException {
        sc.nextLine();
        System.out.println("Choose first team:");
        String firstTeam = sc.nextLine();
        Team validatedFirstTeam = teamController.validateIfTeamExists(firstTeam);
        System.out.println("Choose second team:");
        String secondTeam = sc.nextLine();
        Team validatedSecondTeam = teamController.validateIfTeamExists(secondTeam);
        System.out.println("Chose game time (dd-mm-hh:mm)");
        String gameTime = sc.nextLine();
        String partiallyCheckedGameTime = gameController.checkIfProvidedTimeAreValid(gameTime);
        String validatedGameTime = gameController.checkIfProposeTimeAreFarEnoughFromAnotherGameTime
                (partiallyCheckedGameTime, validatedFirstTeam, validatedSecondTeam);
        gameController.saveNewGameInFileAndDB(validatedFirstTeam, validatedSecondTeam, validatedGameTime);
    }

    private void changeGameTime() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Select game from below list to change game date. Please enter name:");
        gameController.getUpcomingGames();
        String gameName = sc.nextLine();
        String validatedGameName = gameController.checkIfGameExists(gameName);
        System.out.println("Please provide new game time: (format DAY-MONTH-HOUR:MINUTES)");
        String proposedGameTime = sc.nextLine();
        String validatedTime = gameController.validateGameTime(proposedGameTime, validatedGameName);
        gameController.changeTime(validatedGameName, validatedTime);
        System.out.println("Game time changed!");
        Thread.sleep(2000);
    }

    private void showAllPlayersWithTheirTeamsChoice() throws SQLException, IOException, InterruptedException {
        sc.nextLine();
        playerController.showPlayersWithTeams();
        System.out.println("Type \"back\" to get back to menu.");
        String backToMenuInput = sc.nextLine();
        while (!hasBackToMenuChosen(backToMenuInput)) {
            System.out.println("Type \"back\" to get back to menu.");
            backToMenuInput = sc.nextLine();
        }
    }

    private void showAllTeamsChoice() throws SQLException, IOException, InterruptedException {
        sc.nextLine();
        teamController.getAllTeam();
        System.out.println("Type \"back\" to get back to menu.");
        String backToMenuInput = sc.nextLine();
        while (!hasBackToMenuChosen(backToMenuInput)) {
            System.out.println("Type \"back\" to get back to menu.");
            backToMenuInput = sc.nextLine();
        }
    }

    private void showAllGamesChoice() throws SQLException, IOException, InterruptedException {
        sc.nextLine();
        gameController.getAllGames();
        System.out.println("Type \"back\" to get back to menu.");
        String backToMenuInput = sc.nextLine();
        while (!hasBackToMenuChosen(backToMenuInput)) {
            System.out.println("Type \"back\" to get back to menu.");
            backToMenuInput = sc.nextLine();
        }
    }

    private void showUpcomingGamesChoice() throws SQLException, IOException, InterruptedException {
        sc.nextLine();
        gameController.getUpcomingGames();
        System.out.println("Type \"back\" to get back to menu.");
        String backToMenuInput = sc.nextLine();
        while (!hasBackToMenuChosen(backToMenuInput)) {
            System.out.println("Type \"back\" to get back to menu.");
            backToMenuInput = sc.nextLine();
        }
    }

    void getMenu() throws SQLException, IOException, InterruptedException {
        System.out.println("Available actions:");
        System.out.println("""
                1. Create player's account
                2. Create team
                3. Add player to team
                4. Remove player from team
                5. Delete player
                6. Delete team
                7. Register game
                8. Change game time
                9. Show all players
                10. Show all teams
                11. Show all games
                12. Show upcoming games
                """);
        System.out.println("Choose one of above number!");
        while (!sc.hasNextInt()) {
            System.out.println("That's not a number!");
            sc.next();
        }
        int userChoice = sc.nextInt();
        switch (userChoice) {
            case 1 -> createNewPlayerChoice();
            case 2 -> createNewTeamChoice();
            case 3 -> addPlayerToTeamChoice();
            case 4 -> removePlayerFromTeamChoice();
            case 5 -> deletePlayerChoice();
            case 6 -> deleteTeamChoice();
            case 7 -> registerGameChoice();
            case 8 -> changeGameTime();
            case 9 -> showAllPlayersWithTheirTeamsChoice();
            case 10 -> showAllTeamsChoice();
            case 11 -> showAllGamesChoice();
            case 12 -> showUpcomingGamesChoice();
            default -> System.out.println("Incorrect number chosen! Please try again!");
        }
    }

    private boolean hasBackToMenuChosen(String input) throws SQLException, IOException, InterruptedException {
        if (input.equalsIgnoreCase("back")) {
            getMenu();
            return true;
        }
        return false;
    }
}