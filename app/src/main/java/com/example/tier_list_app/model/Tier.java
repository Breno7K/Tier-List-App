package com.example.tier_list_app.model;

import java.util.ArrayList;

public class Tier {

    private String tierlistName;

    private String name;
    private ArrayList<Item> itens;

    int id;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTierlistName() {
        return tierlistName;
    }

    public void setTierlistName(String tierlistName) {
        this.tierlistName = tierlistName;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Item> getItens() {
        return itens;
    }

    public void setItens(ArrayList<Item> itens) {
        this.itens = itens;
    }
}