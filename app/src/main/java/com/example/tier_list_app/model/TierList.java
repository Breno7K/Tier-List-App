package com.example.tier_list_app.model;

import java.io.Serializable;

import java.util.ArrayList;

public class TierList implements Serializable{

    private String username;
    private String name;
    private ArrayList<Tier> tiers;

    int id;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Tier> getTiers() {
        return tiers;
    }

    public void setTiers(ArrayList<Tier> tiers) {
        this.tiers = tiers;
    }

    @Override
    public String toString(){
        return getName();
    }
}
