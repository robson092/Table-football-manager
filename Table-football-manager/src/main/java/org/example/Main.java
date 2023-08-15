package org.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    private static void getPlayerName() {
        sc.nextLine();
        System.out.println("Provide player's name:");
        String playerName = sc.nextLine();
    }

    private static void getTeamName() {
        sc.nextLine();
        System.out.println("Provide team's name:");
        String teamName = sc.nextLine();
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

    private static void getMenu() {
        System.out.println("Choose one of above number!");
        while (!sc.hasNextInt()) {
            System.out.println("That's not a number!");
            sc.next();
        }
        int userChoice = sc.nextInt();
        switch (userChoice) {
            case 1, 4 -> getPlayerName();
            case 2, 6, 8 -> getTeamName();
            case 3 -> getTeamNameAndPlayerName();
            case 5 -> System.out.println("User chose to move player to another team");
            case 7 -> getGameTimeAndTeams();
            case 9 -> System.out.println("User chose to show all teams");
            case 10 -> System.out.println("User chose to show all players");
            case 11 -> System.out.println("User chose to show all scheduled games");
            default -> System.out.println("Incorrect number chosen! Please try again!");
        }
    }

    private static void backToMenu(String input) {
        if (input.equalsIgnoreCase("back")) {
            getMenu();
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Table football manager!");
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

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/","postgres","root")) {
            new DBInitializer(connection).initDB();
            DataLoader dataLoader = new DataLoader(connection);
            dataLoader.loadUsersToDB();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            getMenu();

        }
    }
}