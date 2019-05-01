package com.hakagamesstudio.begreen.pojos;

import java.io.Serializable;

public class CreditCard implements Serializable {

    private String mNumeroTarjeta;
    private int mNumeroSeguridad;
    private String mNameTarjetaHabiente;
    private String mMesTarjeta;
    private String mAnoTarjeta;

    public CreditCard(String mNumeroTarjeta, int mNumeroSeguridad, String mNameTarjetaHabiente, String mMesTarjeta, String mAnoTarjeta) {
        this.mNumeroTarjeta = mNumeroTarjeta;
        this.mNumeroSeguridad = mNumeroSeguridad;
        this.mNameTarjetaHabiente = mNameTarjetaHabiente;
        this.mMesTarjeta = mMesTarjeta;
        this.mAnoTarjeta = mAnoTarjeta;
    }

    public String getmNumeroTarjeta() {
        return mNumeroTarjeta;
    }

    public void setmNumeroTarjeta(String mNumeroTarjeta) {
        this.mNumeroTarjeta = mNumeroTarjeta;
    }

    public int getmNumeroSeguridad() {
        return mNumeroSeguridad;
    }

    public void setmNumeroSeguridad(int mNumeroSeguridad) {
        this.mNumeroSeguridad = mNumeroSeguridad;
    }

    public String getmNameTarjetaHabiente() {
        return mNameTarjetaHabiente;
    }

    public void setmNameTarjetaHabiente(String mNameTarjetaHabiente) {
        this.mNameTarjetaHabiente = mNameTarjetaHabiente;
    }

    public String getmMesTarjeta() {
        return mMesTarjeta;
    }

    public void setmMesTarjeta(String mMesTarjeta) {
        this.mMesTarjeta = mMesTarjeta;
    }

    public String getmAnoTarjeta() {
        return mAnoTarjeta;
    }

    public void setmAnoTarjeta(String mAnoTarjeta) {
        this.mAnoTarjeta = mAnoTarjeta;
    }
}
