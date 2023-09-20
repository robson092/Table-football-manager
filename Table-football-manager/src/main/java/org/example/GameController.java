package org.example;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class GameController {

    private static final Scanner sc = new Scanner(System.in);
    private final GameService gameService = new GameService();

    private String validateIfProperGameTimeFormatProvided(String input) {
        while (!gameService.validateDateTimeFormat(input)) {
            System.out.println("Incorrect format provided! Should be: DAY-MONTH-HOUR:MINUTES");
            input = sc.nextLine();
        }
        return input;
    }

    private String validateIfNumbersInGameTimeAreProper(String input) {
        String inputInProperFormat = validateIfProperGameTimeFormatProvided(input);
        while (!gameService.validateEachNumbersFromGameTime(inputInProperFormat)) {
            System.out.println("Incorrect game time provided! Check whether you exceeded limit of days/months/hours/minutes.");
            inputInProperFormat = sc.nextLine();
        }
        return inputInProperFormat;
    }

    String checkIfProvidedTimeAreValid(String input) {
        String gameTimeWithProperFormat = validateIfNumbersInGameTimeAreProper(input);
        while (!gameService.isGameTimeInPast(gameTimeWithProperFormat)) {
            System.out.println("Game time cannot be earlier then current time. Please provide another game time.");
            gameTimeWithProperFormat = validateIfNumbersInGameTimeAreProper(sc.nextLine());
        }
        return gameTimeWithProperFormat;
    }

    String checkIfProposeTimeAreFarEnoughFromAnotherGameTime(String proposeDateTime, Team firstTeam, Team secondTeam) {
        while (!gameService.isGameTimeHasProperDistanceFromPreviousGame(proposeDateTime, firstTeam, secondTeam)) {
            System.out.println("""
                    One of provided team has already game scheduled less then 30 minutes then provided game time.
                    Games can be scheduled with at least 30 minutes break from each other.
                    Check games scheduler and provide another game time.
                    """);
            proposeDateTime = checkIfProvidedTimeAreValid(sc.nextLine());
        }
        return proposeDateTime;
    }

    void saveNewGameInFileAndDB(Team firstTeam, Team secondTeam, String gameTimeInput) throws IOException {
        LocalDateTime gameTime = gameService.extractDigitToCreateGameTime(gameTimeInput);
        gameService.createNewGame(gameTime, firstTeam, secondTeam);
    }

    private void printGamesDetails(List<Game> games) {
        for (Game game : games) {
            LocalDateTime gameTime = game.getGameTime();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String formattedDate = gameTime.format(dateTimeFormatter);
            System.out.println("GameId: " + game.getId() + "  " + "Participants (name): " + game.getName()
                    + "  " + "Time: " + formattedDate + "  " + "Status: " + game.getGameStatus() + "  "
                    + "Result: " + game.getResult());
        }
    }

    void getAllGames() {
        List<Game> games = gameService.getAllGamesSorted();
        printGamesDetails(games);
    }

    void getUpcomingGames() {
        List<Game> games = gameService.getUpcomingGamesSorted();
        if (checkIfAnyGameScheduled(games)) {
            printGamesDetails(games);
        }
    }

    boolean checkIfAnyGameScheduled(List<Game> games) {
        if (games.isEmpty()) {
            System.out.println("There is no games scheduled.");
            return false;
        }
        return true;
    }

    String checkIfGameExists(String input) {
        String gameName = input;
        while (!gameService.isGameExists(gameName)) {
            System.out.println("Incorrect name provided. Please choose from game list:");
            gameName = sc.nextLine();
        }
        return gameName;
    }

    String validateGameTime(String proposedGameTime, String gameName) {
        String gameTime = checkIfProvidedTimeAreValid(proposedGameTime);
        List<Game> games = gameService.getUpcomingGamesSorted();
        Team firstTeam = games.stream()
                .filter(game -> game.getName().equals(gameName))
                .map(Game::getFirstTeam)
                .findFirst()
                .orElse(null);
        Team secondTeam = games.stream()
                .filter(game -> game.getName().equals(gameName))
                .map(Game::getSecondTeam)
                .findFirst()
                .orElse(null);
        return checkIfProposeTimeAreFarEnoughFromAnotherGameTime(gameTime, firstTeam, secondTeam);
    }

    void changeTime(String gameName, String gameTime) throws IOException {
        List<Game> games = gameService.getAllGamesSorted();
        Game game = games.stream()
                .filter(game1 -> game1.getName().equals(gameName))
                .findFirst()
                .orElse(null);
        gameService.changeGameTime(game, gameTime);
    }
}
