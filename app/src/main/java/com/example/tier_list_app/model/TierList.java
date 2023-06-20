package com.example.tier_list_app.model;

import java.io.Serializable;

import java.util.ArrayList;

public class TierList implements Serializable{
    private String name;
    private ArrayList<Item> itens;

    public TierList(String name) {
        this.name = name;
        this.itens = new ArrayList<>();
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
