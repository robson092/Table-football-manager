package org.example;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameService {

    GameDao gameDao = new GameDao();
    GameRepositoryFile gameRepositoryFile = new GameRepositoryFile();

    boolean isGameTimeInPast(LocalDateTime gameTime) {
        return gameTime.isAfter(LocalDateTime.now());
    }

    boolean isGameTimeHasProperDistanceFromPreviousGame(LocalDateTime proposeDateTime, Team firstTeam, Team secondTeam) {
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
            timeBetweenNewGameAndFirstTeamGame = ChronoUnit.MINUTES.between(firstTeamGameTime, proposeDateTime);
        } else {
            timeBetweenNewGameAndFirstTeamGame = 31;
        }
        if (secondTeamGameTime != null) {
            timeBetweenNewGameAndSecondTeamGame = ChronoUnit.MINUTES.between(secondTeamGameTime, proposeDateTime);
        } else {
            timeBetweenNewGameAndSecondTeamGame = 31;
        }
        return timeBetweenNewGameAndFirstTeamGame >= 30 && timeBetweenNewGameAndSecondTeamGame >= 30;
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
}
