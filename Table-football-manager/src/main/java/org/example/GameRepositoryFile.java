package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.example.DataLoader.*;



public class GameRepositoryFile {

    GameDao gameDao = new GameDao();

    private Map<String, String> fillMapWithGameAttributes(Game game) {
        Map<String, String> newGame = new HashMap<>();
        newGame.put("name", game.getName());
        newGame.put("firstTeamId", String.valueOf(game.getFirstTeam().getId()));
        newGame.put("secondTeamId", String.valueOf(game.getSecondTeam().getId()));
        newGame.put("gameTime", String.valueOf(game.getGameTime()));
        newGame.put("firstTeamGols", String.valueOf(game.getFirstTeamGols()));
        newGame.put("secondTeamGols", String.valueOf(game.getSecondTeamGols()));
        newGame.put("gameStatus", String.valueOf(game.getGameStatus()));
        newGame.put("result", game.getResult());
        return newGame;
    }

    void saveSingleGameToFile(Game game) throws IOException {
        ObjectMapper objectMapper = ObjectMapperProvider.getInstance();
        if (Files.size(PATH_TO_GAMES_FILE) != 0) {
            List<Map<String, String>> fileContent = getFileContent(PATH_TO_GAMES_FILE);
            Map<String, String> newGame = fillMapWithGameAttributes(game);
            fileContent.add(newGame);
            objectMapper.writeValue(PATH_TO_GAMES_FILE.toFile(), fileContent);

        } else {
            Map<String, String> newGame = fillMapWithGameAttributes(game);
            List<Map<String, String>> games = new ArrayList<>();
            games.add(newGame);
            objectMapper.writeValue(PATH_TO_GAMES_FILE.toFile(), games);
        }
    }

    void deleteGameFromFile(Game game) throws IOException {
        String gameName = game.getName();
        ObjectMapper objectMapper = ObjectMapperProvider.getInstance();
        List<Map<String, String>> fileContent = getFileContent(PATH_TO_GAMES_FILE);
        for (Map<String, String> singleGame : fileContent) {
            if (singleGame.get("name").equalsIgnoreCase(gameName)) {
                Set<String> gameMapKeys = singleGame.keySet();
                singleGame.keySet().removeAll(gameMapKeys);
            }
        }
        fileContent.removeIf(Map::isEmpty);
        objectMapper.writeValue(PATH_TO_GAMES_FILE.toFile(), fileContent);
    }

    void updateGameInFile(Game game) throws IOException {
        ObjectMapper objectMapper = ObjectMapperProvider.getInstance();
        List<Map<String, String>> fileContent = getFileContent(PATH_TO_GAMES_FILE);
        for (Map<String, String> singleGame : fileContent) {
            if (singleGame.get("name").equals(game.getName())) {
                singleGame.replace("gameTime", String.valueOf(game.getGameTime()));
                objectMapper.writeValue(PATH_TO_GAMES_FILE.toFile(), fileContent);
            }
        }
    }
}
