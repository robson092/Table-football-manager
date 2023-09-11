package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.example.DataLoader.*;

public class TeamService {

    TeamDao teamDao = new TeamDao();
    PlayerDao playerDao = new PlayerDao();
    TeamRepositoryFile teamRepositoryFile = new TeamRepositoryFile();
    PlayerRepositoryFile playerRepositoryFile = new PlayerRepositoryFile();

    void createTeam(String name) throws IOException {
        Team team = new Team(name);
        teamRepositoryFile.saveSingleTeamInTheFile(team);
        teamDao.save(team);
    }

    boolean checkIfTeamIsFull(String teamName) throws IOException {
        long teamId = 0;
        int teamMemberCount = 0;
        List<Team> teams = teamDao.getAll();
        for (Team team : teams) {
            if (team.getName().equals(teamName)) {
                teamId = team.getId();
                break;
            }
        }
        if (teamId == 0) {
            return true;
        }
        List<Player> players = playerDao.getAll();
        for (Player player : players) {
            if (player.getTeamId() == teamId) {
                teamMemberCount++;
            }
        }
        return teamMemberCount == 2;
    }

    void delete(String name) throws IOException {
        teamRepositoryFile.deleteTeamFromFile(name);
        playerRepositoryFile.updateAllPlayersInFileWhichTeamHasBeenDelete(name);
        teamDao.delete(getTeamByName(name));
    }

    Team getTeamByName(String name) {
        List<Team> teams = teamDao.getAll();
        return teams.stream()
                .filter(player -> player.getName().equals(name))
                .reduce((player, player2) -> player)
                .get();
    }
}
