package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {
    @JsonIgnore
    private int id;
    private String name;
    private String team;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    public void setName(String name) {
        this.name = name;
    }

}
