package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Player {
    @JsonIgnore
    private long id;
    private String name;
    private Integer teamId;
    private int gols;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    public Player(String name, Integer teamId) {
        this.name = name;
        this.teamId = teamId;
    }

    public Player(int id, String name, Integer teamId) {
        this.id = id;
        this.name = name;
        this.teamId = teamId;
    }

    public Player(long id, String name, Integer teamId, int gols) {
        this.id = id;
        this.name = name;
        this.teamId = teamId;
        this.gols = gols;
    }

    public long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public int getGols() {
        return gols;
    }

    public void setGols(int gols) {
        this.gols = gols;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teamId=" + teamId +
                ", gols=" + gols +
                '}';
    }
}
