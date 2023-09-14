package org.example;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class GameController {

    private static final Scanner sc = new Scanner(System.in);
    GameService gameService = new GameService();
    TeamService teamService = new TeamService();

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
}
