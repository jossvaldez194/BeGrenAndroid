package com.hakagamesstudio.begreen.pojos;

import java.io.Serializable;

public class OrchardFavorites implements Serializable {

    private String orchards_id;
    private String orchards_userId;
    private String orchards_name;
    private String orchards_favorite;
    private String orchards_image;

    public String getOrchards_id() {
        return orchards_id;
    }

    public void setOrchards_id(String orchards_id) {
        this.orchards_id = orchards_id;
    }

    public String getOrchards_name() {
        return orchards_name;
    }

    public void setOrchards_name(String orchards_name) {
        this.orchards_name = orchards_name;
    }

    public String getOrchards_favorite() {
        return orchards_favorite;
    }

    public void setOrchards_favorite(String orchards_favorite) {
        this.orchards_favorite = orchards_favorite;
    }

    public String getOrchards_image() {
        return orchards_image;
    }

    public void setOrchards_image(String orchards_image) {
        this.orchards_image = orchards_image;
    }

    public String getOrchards_userId() {
        return orchards_userId;
    }

    public void setOrchards_userId(String orchards_userId) {
        this.orchards_userId = orchards_userId;
    }
}
