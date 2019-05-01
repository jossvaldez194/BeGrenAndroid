package com.hakagamesstudio.begreen.pojos;

import java.io.Serializable;

public class Offerts implements Serializable {

    private int image;

    public Offerts(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
