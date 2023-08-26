package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Player {
    @JsonIgnore
    private int id;
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

    public int getId() {
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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", team='" + teamId + '\'' +
                '}';
    }
}
