package org.example;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameService {

    private final GameDao gameDao = new GameDao();
    private final GameRepositoryFile gameRepositoryFile = new GameRepositoryFile();
    private final GameStatusManager gameStatusManager = new GameStatusManager();

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
        long id = gameDao.save(game);
        game.setId(id);
        gameRepositoryFile.saveSingleGameToFile(game);
    }

    List<Game> getAllGamesSorted() throws IOException {
        gameStatusManager.updateInDB();
        gameStatusManager.updateInFile();
        List<Game> games = gameDao.getAllSortedByGameTime();
        for (Game game : games) {
            String result = game.getResult() == null ? "TBA" : game.getResult();
            game.setResult(result);
        }
        return games;
    }

    List<Game> getUpcomingGamesSorted() {
        List<Game> games = gameDao.getAllSortedByGameTime();
        for (Game game : games) {
            String result = game.getResult() == null ? "TBA" : game.getResult();
            game.setResult(result);
        }
        games.removeIf(game -> game.getGameTime()
                .isBefore(LocalDateTime.now()));
        return games;
    }

    List<Game> getAlreadyStartedGameSorted() throws IOException {
        List<Game> games = gameDao.getAllSortedByGameTime();
        for (Game game : games) {
            String result = game.getResult() == null ? "TBA" : game.getResult();
            game.setResult(result);
        }
        games.removeIf(game -> game.getGameTime()
                .isAfter(LocalDateTime.now()));
        games.removeIf(game -> !game.getResult().equals("TBA"));
        gameStatusManager.updateInDB();
        gameStatusManager.updateInFile();
        return games;
    }

    boolean isUpcomingGameExists(String name) {
        List<Game> games = getUpcomingGamesSorted();
        for (Game game : games) {
            if (game.getId() == Integer.parseInt(name)) {
                return true;
            }
        }
        return false;
    }

    boolean isStartedGameExist(String name) throws IOException {
        List<Game> games = getAlreadyStartedGameSorted();
        for (Game game : games) {
            if (game.getId() == Integer.parseInt(name)) {
                return true;
            }
        }
        return false;
    }

    void changeGameTime(Game game, String gameTime) throws IOException {
        LocalDateTime correctFormatDate = extractDigitToCreateGameTime(gameTime);
        game.setGameTime(correctFormatDate);
        game.setResult("TBA");
        gameRepositoryFile.updateGameTimeInFile(game);
        gameRepositoryFile.updateGameResultInFile(game);
        gameDao.updateDate(game, Timestamp.valueOf(correctFormatDate));
        gameDao.update(game, new String[] {"result", null});
    }

    boolean validateIfPositiveNumberEnter(int input) {
        String stringInput = String.valueOf(input);
        String regex = "^\\d*[1-9]\\d*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(stringInput);
        return matcher.matches();
    }

    void submitScore(Game game, int firstTeamGols, int secondTeamGols) throws IOException {
        game.setFirstTeamGols(firstTeamGols);
        game.setSecondTeamGols(secondTeamGols);
        game.setGameStatus(GameStatus.FINISHED);
        if (game.getFirstTeamGols() > game.getSecondTeamGols()) {
            game.setResult(game.getFirstTeam().getName() + " WON");
        } else if (game.getSecondTeamGols() > game.getFirstTeamGols()) {
            game.setResult(game.getSecondTeam().getName() + " WON");
        } else {
            game.setResult("DRAW");
        }
        gameRepositoryFile.updateGameScoreInFile(game);
        gameRepositoryFile.updateGameResultInFile(game);
        gameRepositoryFile.updateGameStatusInFile(game);
        gameDao.updateScore(game, new int[] {firstTeamGols, secondTeamGols});
        gameDao.update(game, new String[] {"result", game.getResult()});
        gameDao.update(game, new String[] {"game_status", String.valueOf(game.getGameStatus())});
    }

    void deleteGameFromDbAndFile(long id) throws IOException {
        List<Game> games = gameDao.getAll();
        Game requestedGame = games.stream()
                .filter(game -> game.getId() == id)
                .findFirst()
                .orElse(null);
        gameDao.delete(requestedGame);
        gameRepositoryFile.deleteGameFromFile(requestedGame);
    }
}
