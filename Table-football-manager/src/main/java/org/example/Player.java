package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Player {
    @JsonIgnore
    private long id;
    private String name;
    private Integer teamId;

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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", team='" + teamId + '\'' +
                '}';
    }
}
