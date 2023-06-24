package com.example.tier_list_app.model;

import java.io.Serializable;

import java.util.ArrayList;

public class TierList implements Serializable{

    private String userEmail;
    private String name;
    private ArrayList<Tier> tiers;

    String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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
