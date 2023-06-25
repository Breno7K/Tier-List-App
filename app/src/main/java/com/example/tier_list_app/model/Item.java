package com.example.tier_list_app.model;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable {

    private String id;
    private String name;
    private String tierId;
    private String imageUrl;

    private Date createdAt;


    public Item() {
    }

    public Item(String id, String name, String tierId, String imageUrl) {
        this.id = id;
        this.name = name;
        this.tierId = tierId;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String urlItem) {
        this.imageUrl = urlItem;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
