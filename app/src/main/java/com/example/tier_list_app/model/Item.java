package com.example.tier_list_app.model;

import java.io.Serializable;

public class Item implements Serializable {

    private String tierName;

    private String name;
    private String urlItem;

    String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tier) {
        this.tierName = tier;
    }

    public String getUrlItem() {
        return urlItem;
    }

    public void setUrlItem(String urlItem) {
        this.urlItem = urlItem;
    }
}
