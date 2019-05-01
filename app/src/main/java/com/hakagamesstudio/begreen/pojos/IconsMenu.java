package com.hakagamesstudio.begreen.pojos;

import java.io.Serializable;

public class IconsMenu implements Serializable {

    private int items;
    private String title;

    public IconsMenu(int items, String titles) {
        this.setItems(items);
        this.setTitle(titles);
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
