package com.hakagamesstudio.begreen.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllOffertsList implements Serializable {

    private List<infoOfferts> listData = new ArrayList<>();

    public List<infoOfferts> getListData() {
        return listData;
    }

    public void setListData(List<infoOfferts> listData) {
        this.listData = listData;
    }
}
