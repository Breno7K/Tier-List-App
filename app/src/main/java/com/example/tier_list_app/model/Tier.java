package com.example.tier_list_app.model;

import java.util.ArrayList;

public class Tier {

    private String tierlistId;

    private String name;
    private ArrayList<Item> itens;

    private String color;

    String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTierlistId() {
        return tierlistId;
    }

    public void setTierlistId(String tierlistName) {
        this.tierlistId = tierlistName;
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

    public String getColor(){
        return this.color;
    }

    public void setColor(String color){
        this.color = color;
    }
}