package com.hakagamesstudio.begreen.pojos;

import java.io.Serializable;

public class OrchardsProductSingle implements Serializable {

    public static final int CATEGORY=0;
    public static final int PRODUCT=1;

    private int type;
    private String titleCatery;
    private String titleProduct;

    public OrchardsProductSingle(int type, String titleCatery, String titleProduct) {
        this.setType(type);
        this.setTitleCatery(titleCatery);
        this.setTitleProduct(titleProduct);
    }

    public int getType() {
        return type;
    }

    private void setType(int type) {
        this.type = type;
    }

    public String getTitleCatery() {
        return titleCatery;
    }

    private void setTitleCatery(String titleCatery) {
        this.titleCatery = titleCatery;
    }

    public String getTitleProduct() {
        return titleProduct;
    }

    private void setTitleProduct(String titleProduct) {
        this.titleProduct = titleProduct;
    }

}
