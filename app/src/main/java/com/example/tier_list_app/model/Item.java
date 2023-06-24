package com.example.tier_list_app.model;

import java.io.Serializable;

public class Item implements Serializable {

    private String id;
    private String name;
    private String tierId;
    private String urlItem;

    public Item() {
        // Default constructor required for Firestore
    }

    public Item(String id, String name, String tierId, String urlItem) {
        this.id = id;
        this.name = name;
        this.tierId = tierId;
        this.urlItem = urlItem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTierId() {
        return tierId;
    }

    public void setTierId(String tierId) {
        this.tierId = tierId;
    }

    public String getUrlItem() {
        return urlItem;
    }

    public void setUrlItem(String urlItem) {
        this.urlItem = urlItem;
    }
}
