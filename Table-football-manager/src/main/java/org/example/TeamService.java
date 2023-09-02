package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.example.DataLoader.*;

public class TeamService {

    boolean checkIfTeamIsFull(String teamName) throws IOException {
        TeamRepositoryDB teamRepositoryDB = new TeamRepositoryDB();
        int teamId = 0;
        int teamMemberCount = 0;
        List<Map<String, String>> allTeams = teamRepositoryDB.getAllTeams();
        for (Map<String, String> team : allTeams) {
            if (Objects.equals(team.get("team"), teamName)) {
                teamId = Integer.parseInt(team.get("id"));
            }
        }
        List<Map<String, Object>> fileContent = getFileContent(PATH_TO_USERS_FILE);
        if (DataLoader.checkIfAlreadyExistsInTheFile(teamName, PATH_TO_TEAMS_FILE)) {
            for (Map<String, Object> mapWithPlayers : fileContent) {
                if (Objects.equals(mapWithPlayers.get("teamId"), teamId)) {
                    teamMemberCount++;
                }
            }
            return teamMemberCount == 2;
        }
        return false;
    }
}
