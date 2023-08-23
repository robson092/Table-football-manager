package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Team {
    @JsonIgnore
    private int id;
    private String name;
    private List<User> users;

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

    public List<User> getUsers() {
        return users;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
