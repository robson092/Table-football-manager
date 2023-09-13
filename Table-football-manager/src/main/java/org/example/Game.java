package org.example;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Game {
    private long id;
    private String name;
    private Team firstTeam;
    private Team secondTeam;
    private LocalDateTime gameTime;
    private int firstTeamGols;
    private int secondTeamGols;
    private GameStatus gameStatus;
    private String result;

    public Game() {
    }

    public Game(Team firstTeam, Team secondTeam, LocalDateTime gameTime) {
        this.name = firstTeam.getName() + " vs " + secondTeam.getName();
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.gameTime = gameTime;
        this.gameStatus = GameStatus.SCHEDULED;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team getFirstTeam() {
        return firstTeam;
    }

    public void setFirstTeam(Team firstTeam) {
        this.firstTeam = firstTeam;
    }

    public Team getSecondTeam() {
        return secondTeam;
    }

    public void setSecondTeam(Team secondTeam) {
        this.secondTeam = secondTeam;
    }

    public LocalDateTime getGameTime() {
        return gameTime;
    }

    public void setGameTime(LocalDateTime gameTime) {
        this.gameTime = gameTime;
    }

    public int getFirstTeamGols() {
        return firstTeamGols;
    }

    public void setFirstTeamGols(int firstTeamGols) {
        this.firstTeamGols = firstTeamGols;
    }

    public int getSecondTeamGols() {
        return secondTeamGols;
    }

    public void setSecondTeamGols(int secondTeamGols) {
        this.secondTeamGols = secondTeamGols;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", firstTeam=" + firstTeam +
                ", secondTeam=" + secondTeam +
                ", gameTime=" + gameTime +
                ", firstTeamGols=" + firstTeamGols +
                ", secondTeamGols=" + secondTeamGols +
                ", gameStatus=" + gameStatus +
                ", result='" + result + '\'' +
                '}';
    }
}
