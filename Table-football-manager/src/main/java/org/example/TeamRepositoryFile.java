package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.DataLoader.PATH_TO_TEAMS_FILE;
import static org.example.DataLoader.getFileContent;

public class TeamRepositoryFile {

    void saveSingleTeamInTheFile(Team team) throws IOException {
        ObjectMapper objectMapper = ObjectMapperProvider.getInstance();
        if (Files.size(PATH_TO_TEAMS_FILE) != 0) {
            List<Map<String, Object>> listOfTeams = getFileContent(PATH_TO_TEAMS_FILE);
            Map<String, Object> newTeam = new HashMap<>();
            newTeam.put("name", team.getName());
            listOfTeams.add(newTeam);
            objectMapper.writeValue(PATH_TO_TEAMS_FILE.toFile(), listOfTeams);
        } else {
            Map<String, String> newTeam = new HashMap<>();
            newTeam.put("name", team.getName());
            List<Map<String, String>> listOfTeams = new ArrayList<>();
            listOfTeams.add(newTeam);
            objectMapper.writeValue(PATH_TO_TEAMS_FILE.toFile(), listOfTeams);
        }
    }

    void deleteTeamFromFile(String teamName) throws IOException {
        ObjectMapper objectMapper = ObjectMapperProvider.getInstance();
        List<Map<String, Object>> fileContent = getFileContent(PATH_TO_TEAMS_FILE);
        for (Map<String, Object> singleMapWithTeam : fileContent) {
            if (singleMapWithTeam.get("name").equals(teamName)) {
                singleMapWithTeam.remove("name");
            }
        }
        fileContent.removeIf(Map::isEmpty);
        objectMapper.writeValue(PATH_TO_TEAMS_FILE.toFile(), fileContent);
    }
}
