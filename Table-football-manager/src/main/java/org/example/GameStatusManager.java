package org.example;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class GameStatusManager {
    private final GameDao gameDao = new GameDao();
    private final GameRepositoryFile gameRepositoryFile = new GameRepositoryFile();

    private List<Game> updateGamesStatus() {
        List<Game> games = gameDao.getAll();
        LocalDateTime now = LocalDateTime.now();
        for (Game singleGame : games) {
            if (singleGame.getGameTime().isAfter(now) || singleGame.getGameTime().equals(now)) {
                singleGame.setGameStatus(GameStatus.STARTED);
            }
        }
        return games;
    }

    void updateInDB() {
        List<Game> games = updateGamesStatus();
        for (Game game : games) {
            gameDao.update(game, new String[] {"game_status", String.valueOf(game.getGameStatus())});
        }
    }

    void updateInFile() throws IOException {
        List<Game> games = updateGamesStatus();
        for (Game game : games) {
            gameRepositoryFile.updateGameStatusInFile(game);
        }
    }
}
