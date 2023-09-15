package org.example;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameService {

    private final GameDao gameDao = new GameDao();
    private final GameRepositoryFile gameRepositoryFile = new GameRepositoryFile();

    boolean isGameTimeInPast(String input) {
        LocalDateTime gameTime = extractDigitToCreateGameTime(input);
        return gameTime.isAfter(LocalDateTime.now());
    }

    boolean isGameTimeHasProperDistanceFromPreviousGame(String proposeDateTime, Team firstTeam, Team secondTeam) {
        LocalDateTime firstTeamGameTime = null;
        LocalDateTime secondTeamGameTime = null;
        long timeBetweenNewGameAndFirstTeamGame = 0;
        long timeBetweenNewGameAndSecondTeamGame = 0;
        List<Game> games = gameDao.getAll();
        for (Game singleGame : games) {
            if (singleGame.getFirstTeam().getId() == firstTeam.getId() ||
                    singleGame.getSecondTeam().getId() == firstTeam.getId()) {
                firstTeamGameTime = singleGame.getGameTime();
            }
            if (singleGame.getSecondTeam().getId() == secondTeam.getId() ||
                    singleGame.getFirstTeam().getId() == secondTeam.getId()) {
                secondTeamGameTime = singleGame.getGameTime();
            }
        }
        if (firstTeamGameTime != null) {
            timeBetweenNewGameAndFirstTeamGame = ChronoUnit.MINUTES.between
                    (firstTeamGameTime, extractDigitToCreateGameTime(proposeDateTime));
        } else {
            timeBetweenNewGameAndFirstTeamGame = 31;
        }
        if (secondTeamGameTime != null) {
            timeBetweenNewGameAndSecondTeamGame = ChronoUnit.MINUTES.between
                    (secondTeamGameTime, extractDigitToCreateGameTime(proposeDateTime));
        } else {
            timeBetweenNewGameAndSecondTeamGame = 31;
        }
        return Math.abs(timeBetweenNewGameAndFirstTeamGame) >= 30 && Math.abs(timeBetweenNewGameAndSecondTeamGame) >= 30;
    }

    boolean validateDateTimeFormat(String input) {
        Pattern pattern = Pattern.compile("\\d{2}-\\d{2}-\\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    boolean validateEachNumbersFromGameTime(String input) {
        String[] splitDate = input.split("-|:");
        return Integer.parseInt(splitDate[0]) <= 31 && Integer.parseInt(splitDate[1]) <= 12
                && Integer.parseInt(splitDate[2]) <= 24 && Integer.parseInt(splitDate[3]) <= 59;
    }

    LocalDateTime extractDigitToCreateGameTime(String input) {
        String[] splitDate = input.split("-|:");
        int[] dateNum = new int[4];
        for (int i = 0; i < 4; i++) {
            int j = Integer.parseInt(splitDate[i]);
            dateNum[i] = j;
        }
        return LocalDateTime.of(2023, dateNum[1], dateNum[0], dateNum[2], dateNum[3]);
    }

    void createNewGame(LocalDateTime gameTime, Team firstTeam, Team secondTeam) throws IOException {
        Game game = new Game(firstTeam, secondTeam, gameTime);
        gameDao.save(game);
        gameRepositoryFile.saveSingleGameToFile(game);
    }

    List<Game> getAllGamesSorted() {
        List<Game> games = gameDao.getAllSortedByGameTime();
        for (Game game : games) {
            String result = game.getResult() == null ? "TBA" : game.getResult();
            game.setResult(result);
        }
        return games;
    }

    List<Game> getUpcomingGamesSorted() {
        List<Game> games = gameDao.getAllSortedByGameTime();
        games.removeIf(game -> game.getGameTime().isBefore(LocalDateTime.now()));
        return games;
    }
}
