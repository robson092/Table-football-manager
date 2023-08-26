package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Team {
    @JsonIgnore
    private int id;
    private String name;
    private List<Player> players;

    public Team() {
    }

    public Team(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Player> getUsers() {
        return players;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsers(List<Player> players) {
        this.players = players;
    }
}
