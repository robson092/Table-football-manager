package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static org.example.DataLoader.checkIfAlreadyExistsInTheFile;

public class TeamController {
    private static final Scanner sc = new Scanner(System.in);

    TeamDao teamDao = new TeamDao();
    TeamService teamService = new TeamService();

    void createTeam(String name) throws IOException {
        while (checkIfAlreadyExistsInTheFile(name, DataLoader.PATH_TO_TEAMS_FILE)) {
            System.out.println("Team is already exists. Please provide another name.");
            name = sc.nextLine();
        }
        teamService.createTeam(name);
    }

    String validateTeam(String name) throws IOException {
        while (teamService.checkIfTeamIsFull(name)) {
            System.out.println("Incorrect team provided! Please provide another team.");
            name = sc.nextLine();
        }
        return name;
    }

    Team validateIfTeamExists(String name) throws IOException {
        while (!teamService.checkIfTeamExists(name)) {
            System.out.println("Team does not exists. Please provide another team.");
            name = sc.nextLine();
        }
        return teamService.getTeamByName(name);
    }

    void deleteTeam(String name) throws IOException {
        String validatedTeam = validateTeam(name);
        teamService.delete(validatedTeam);
    }

    void getAllTeam() {
        List<Team> teams = teamDao.getAll();
        for (Team team : teams) {
            System.out.println("Name: " + team.getName());
        }
    }
}
