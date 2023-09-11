package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Menu {
    private static final Scanner sc = new Scanner(System.in);
    private static final PlayerController playerController = new PlayerController();
    private static final TeamController teamController = new TeamController();

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

    private void registerGame() {
        sc.nextLine();
        String gameTime = sc.nextLine();
        System.out.println("Choose first team:");
        String firstTeam = sc.nextLine();
        System.out.println("Choose second team:");
        String secondTeam = sc.nextLine();
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
                8. Edit game
                9. Show all players
                10. Show all teams
                11. Show game scheduler
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
            case 7 -> registerGame();
            case 8 -> System.out.println("User chose to edit game");
            case 9 -> showAllPlayersWithTheirTeamsChoice();
            case 10 -> showAllTeamsChoice();
            case 11 -> System.out.println("User chose to show all scheduled games");
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