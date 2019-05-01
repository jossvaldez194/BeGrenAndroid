package com.hakagamesstudio.begreen.pojos.device_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class AppSettingsData {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("data")
    @Expose
    private List<AppSettingsDetails> appDetails = null;
    @SerializedName("message")
    @Expose
    private String message;

    
    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<AppSettingsDetails> getProductData() {
        return appDetails;
    }

    public void setProductData(List<AppSettingsDetails> appDetails) {
        this.appDetails = appDetails;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
