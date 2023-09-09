package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.example.DataLoader.*;

public class TeamService {

    TeamDao teamDao = new TeamDao();

    boolean checkIfTeamIsFull(String teamName) throws IOException {
        long teamId = 0;
        int teamMemberCount = 0;
        List<Team> teams = teamDao.getAll();
        for (Team team : teams) {
            if (team.getName().equals(teamName)) {
                teamId = team.getId();
                break;
            }
            if (teamId == 0) {
                return true;
            }
        }
        List<Map<String, Object>> fileContent = getFileContent(PATH_TO_USERS_FILE);
        if (DataLoader.checkIfAlreadyExistsInTheFile(teamName, PATH_TO_TEAMS_FILE)) {
            for (Map<String, Object> mapWithPlayers : fileContent) {
                if (Objects.equals(mapWithPlayers.get("teamId"), teamId)) {
                    teamMemberCount++;
                }
            }
        }
        return teamMemberCount == 2;
    }

    Team getTeamByName(String name) {
        List<Team> teams = teamDao.getAll();
        return teams.stream()
                .filter(player -> player.getName().equals(name))
                .reduce((player, player2) -> player)
                .get();
    }
}
