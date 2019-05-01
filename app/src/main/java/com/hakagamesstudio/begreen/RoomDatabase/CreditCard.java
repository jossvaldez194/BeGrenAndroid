package com.hakagamesstudio.begreen.RoomDatabase;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "creditCard")
public class CreditCard implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "idUser")
    private String idUser;
    @ColumnInfo(name = "nameUser")
    private String nameUser;
    @ColumnInfo(name = "numberTarjet")
    private String numberTarjet;
    @ColumnInfo(name = "month")
    private String month;
    @ColumnInfo(name = "year")
    private String year;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getNumberTarjet() {
        return numberTarjet;
    }

    public void setNumberTarjet(String numberTarjet) {
        this.numberTarjet = numberTarjet;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
