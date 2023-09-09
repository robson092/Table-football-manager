package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static org.example.DataLoader.checkIfAlreadyExistsInTheFile;

public class Menu {
    private static final Scanner sc = new Scanner(System.in);
    private static final PlayerDao PLAYER_DAO = new PlayerDao();
    private static final PlayerRepositoryFile playerRepositoryFile = new PlayerRepositoryFile();
    private static final TeamDao TEAM_DAO = new TeamDao();
    private static final TeamRepositoryFile teamRepositoryFile = new TeamRepositoryFile();
    private static final GameDao GAME_DAO = new GameDao();
    private static final TeamService teamService = new TeamService();
    private static final PlayerService playerService = new PlayerService();

    private void createNewPlayer() throws IOException, SQLException, InterruptedException {
        sc.nextLine();
        System.out.println("Provide player's name:");
        String playerName = sc.nextLine();
        while (checkIfAlreadyExistsInTheFile(playerName, DataLoader.PATH_TO_USERS_FILE)) {
            System.out.println("Player is already exists. Please provide another name.");
            playerName = sc.nextLine();
        }
        Player player = new Player(playerName);
        playerRepositoryFile.saveSinglePlayerInTheFile(player);
        PLAYER_DAO.save(player);
        System.out.println("Player has been successfully created!");
        Thread.sleep(2000);
    }

    private void createNewTeam() throws SQLException, IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Provide team's name:");
        String teamName = sc.nextLine();
        while (checkIfAlreadyExistsInTheFile(teamName, DataLoader.PATH_TO_TEAMS_FILE)) {
            System.out.println("Team is already exists. Please provide another name.");
            teamName = sc.nextLine();
        }
        Team team = new Team(teamName);
        teamRepositoryFile.saveSingleTeamInTheFile(team);
        TEAM_DAO.save(team);
        System.out.println("Player has been successfully created!");
        Thread.sleep(2000);
    }

    private void addPlayerToTeam() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Provide team's name:");
        String teamName = sc.nextLine();
        while (teamService.checkIfTeamIsFull(teamName)) {
            System.out.println("There is either no such team or team is full. Please provide another team.");
            teamName = sc.nextLine();
        }
        System.out.println("Provide player's name:");
        String playerName = sc.nextLine();
        while (!playerService.checkIfPlayerExistsAndHasNoTeam(playerName)) {
            System.out.println("Provided player either does not exist or already has a team. Please provide another player.");
            playerName = sc.nextLine();
        }
        int playerId = PLAYER_DAO.updatePlayerWithTeamIdInDB(playerName, teamName);
        playerRepositoryFile.saveSinglePlayerInTheFile(new Player(playerName, playerId));
        System.out.println("Player has been successfully added to the team.");
        Thread.sleep(2000);
    }

    private void choosePlayerToRemoveFromTeam() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Please provide player name to remove from team.");
        String playerName = sc.nextLine();
        while (playerService.checkIfPlayerExistsAndHasNoTeam(playerName)) {
            System.out.println("Provided player either does not exists or does not has a team. Please provide another player.");
            playerName = sc.nextLine();
        }
        Player chosenPlayer = playerService.getPlayerByName(playerName);
        PLAYER_DAO.update(chosenPlayer, new String[] {"team_Id", null} );
        chosenPlayer.setTeamId(null);
        playerRepositoryFile.saveSinglePlayerInTheFile(chosenPlayer);
        System.out.println("Player has been successfully removed from the team.");
        Thread.sleep(2000);
    }

    private void deletePlayer() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Please provide player name to delete from application.");
        String playerName = sc.nextLine();
        while (!checkIfAlreadyExistsInTheFile(playerName, DataLoader.PATH_TO_USERS_FILE)) {
            System.out.println("Provided player does not exists. Please provide another player.");
            playerName = sc.nextLine();
        }
        playerRepositoryFile.deletePlayerFromFile(playerName);
        PLAYER_DAO.delete(playerService.getPlayerByName(playerName));
        System.out.println("Player has been successfully deleted from application.");
        Thread.sleep(2000);
    }

    private void deleteTeam() throws IOException, InterruptedException {
        sc.nextLine();
        System.out.println("Please provide team name to delete from application.");
        String teamName = sc.nextLine();
        while (!checkIfAlreadyExistsInTheFile(teamName, DataLoader.PATH_TO_TEAMS_FILE)) {
            System.out.println("Provided team does not exists. Please provide another team.");
            teamName = sc.nextLine();
        }
        teamRepositoryFile.deleteTeamFromFile(teamName);
        playerRepositoryFile.updateAllPlayersInFileWhichTeamHasBeenDelete(teamName);
        TEAM_DAO.delete(teamService.getTeamByName(teamName));
        System.out.println("Team has been successfully deleted from application.");
        Thread.sleep(2000);
    }

    private void registerGame() {
        sc.nextLine();
//        String patter = "\\d{2}-\\d{2}";
//        System.out.println("Provide game time (dd-mm):");
//        while (!sc.hasNext(Pattern.compile(patter))) {
//            System.out.println("Incorrect game time format!");
//            sc.next();
//        }
        //String gameTime = sc.nextLine();
        System.out.println("Choose first team:");
        String firstTeam = sc.nextLine();
        System.out.println("Choose second team:");
        String secondTeam = sc.nextLine();
        Game game = new Game(teamService.getTeamByName(firstTeam),
                teamService.getTeamByName(secondTeam), new Timestamp(new Date().getTime()));
        GAME_DAO.save(game);
    }

    private void showAllPlayersWithTheirTeams() throws SQLException, IOException, InterruptedException {
        sc.nextLine();
        List<Player> players = PLAYER_DAO.getAll();
        List<Team> teams = TEAM_DAO.getAll();
        Map<String, String> playersWithTeams = new HashMap<>();
        for (Player player : players) {
            for (Team team : teams) {
                if (player.getTeamId() == team.getId()) {
                    playersWithTeams.put(player.getName(), team.getName());
                }
                if (player.getTeamId() == 0) {
                    playersWithTeams.put(player.getName(), "No Team");
                }
            }
        }
        for (Map.Entry<String, String> entry : playersWithTeams.entrySet()) {
            System.out.println("Player:" + entry.getKey() + "   Team: " + entry.getValue());
        }
        System.out.println("Type \"back\" to get back to menu.");
        String backToMenuInput = sc.nextLine();
        while (!backToMenu(backToMenuInput)) {
            System.out.println("Type \"back\" to get back to menu.");
            backToMenuInput = sc.nextLine();
        }
    }

    private void showAllTeams() throws SQLException, IOException, InterruptedException {
        sc.nextLine();
        List<Team> teams = TEAM_DAO.getAll();
        for (Team team : teams) {
            System.out.println("Name: " + team.getName());
        }
        System.out.println("Type \"back\" to get back to menu.");
        String backToMenuInput = sc.nextLine();
        while (!backToMenu(backToMenuInput)) {
            System.out.println("Type \"back\" to get back to menu.");
            backToMenuInput = sc.nextLine();
        }
    }

    void getMenu() throws SQLException, IOException, InterruptedException {
        System.out.println("Available actions:");
        System.out.println("""
                1. Create player's account
                2. Create team
                3. Add player to team
                4. Remove player from team
                5. Delete player
                6. Delete team
                7. Register game
                8. Edit game
                9. Show all players
                10. Show all teams
                11. Show game scheduler
                """);
        System.out.println("Choose one of above number!");
        while (!sc.hasNextInt()) {
            System.out.println("That's not a number!");
            sc.next();
        }
        int userChoice = sc.nextInt();
        switch (userChoice) {
            case 1 -> createNewPlayer();
            case 2 -> createNewTeam();
            case 3 -> addPlayerToTeam();
            case 4 -> choosePlayerToRemoveFromTeam();
            case 5 -> deletePlayer();
            case 6 -> deleteTeam();
            case 7 -> registerGame();
            case 8 -> System.out.println("User chose to edit game");
            case 9 -> showAllPlayersWithTheirTeams();
            case 10 -> showAllTeams();
            case 11 -> System.out.println("User chose to show all scheduled games");
            default -> System.out.println("Incorrect number chosen! Please try again!");
        }
    }

    private boolean backToMenu(String input) throws SQLException, IOException, InterruptedException {
        if (input.equalsIgnoreCase("back")) {
            getMenu();
            return true;
        }
        return false;
    }
}