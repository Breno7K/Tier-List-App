package com.example.tier_list_app.model;
import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{

    String name, email, username, password;
    private ArrayList<TierList> tierLists;
    String id;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String usuario) {
        this.username = usuario;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<TierList> getTierLists() {
        return tierLists;
    }

    public void setTierLists(ArrayList<TierList> tierLists) {
        this.tierLists = tierLists;
    }


}
