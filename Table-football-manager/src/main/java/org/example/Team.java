package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Team {
    @JsonIgnore
    private long id;
    private String name;
    private List<Player> players;

    public Team() {
    }

    public Team(String name) {
        this.name = name;
    }

    public Team(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
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
