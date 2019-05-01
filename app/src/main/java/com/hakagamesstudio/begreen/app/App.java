package com.hakagamesstudio.begreen.app;


import android.app.Application;
import android.content.Context;

import com.hakagamesstudio.begreen.databases.DB_Handler;
import com.hakagamesstudio.begreen.databases.DB_Manager;
import com.hakagamesstudio.begreen.pojos.address_model.AddressDetails;
import com.hakagamesstudio.begreen.pojos.banner_model.BannerDetails;
import com.hakagamesstudio.begreen.pojos.category_model.CategoryDetails;
import com.hakagamesstudio.begreen.pojos.device_model.AppSettingsDetails;
import com.hakagamesstudio.begreen.pojos.drawer_model.Drawer_Items;
import com.hakagamesstudio.begreen.pojos.pages_model.PagesDetails;
import com.hakagamesstudio.begreen.pojos.shipping_model.ShippingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * App extending Application, is used to save some Lists and Objects with Application Context.
 **/


public class App extends Application {

    // Application Context
    private static Context context;
    private static DB_Handler db_handler;

    private List<Drawer_Items> drawerHeaderList;
    private Map<Drawer_Items, List<Drawer_Items>> drawerChildList;


    private AppSettingsDetails appSettingsDetails = null;
    private List<BannerDetails> bannersList = new ArrayList<>();
    private List<CategoryDetails> categoriesList = new ArrayList<>();
    private List<PagesDetails> staticPagesDetails = new ArrayList<>();

    private String tax = "";
    private ShippingService shippingService = null;
    private AddressDetails shippingAddress = new AddressDetails();
    private AddressDetails billingAddress = new AddressDetails();

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        // initialize DB_Handler and DB_Manager
        db_handler = new DB_Handler();
        DB_Manager.initializeInstance(db_handler);
    }

    //*********** Returns Application Context ********//

    public static Context getContext() {
        return context;
    }

    public List<Drawer_Items> getDrawerHeaderList() {
        return drawerHeaderList;
    }

    public void setDrawerHeaderList(List<Drawer_Items> drawerHeaderList) {
        this.drawerHeaderList = drawerHeaderList;
    }

    public Map<Drawer_Items, List<Drawer_Items>> getDrawerChildList() {
        return drawerChildList;
    }

    public void setDrawerChildList(Map<Drawer_Items, List<Drawer_Items>> drawerChildList) {
        this.drawerChildList = drawerChildList;
    }


    public AppSettingsDetails getAppSettingsDetails() {
        return appSettingsDetails;
    }

    public void setAppSettingsDetails(AppSettingsDetails appSettingsDetails) {
        this.appSettingsDetails = appSettingsDetails;
    }

    public List<BannerDetails> getBannersList() {
        return bannersList;
    }

    public void setBannersList(List<BannerDetails> bannersList) {
        this.bannersList = bannersList;
    }

    public List<CategoryDetails> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(List<CategoryDetails> categoriesList) {
        this.categoriesList = categoriesList;
    }

    public List<PagesDetails> getStaticPagesDetails() {
        return staticPagesDetails;
    }

    public void setStaticPagesDetails(List<PagesDetails> staticPagesDetails) {
        this.staticPagesDetails = staticPagesDetails;
    }


    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public ShippingService getShippingService() {
        return shippingService;
    }

    public void setShippingService(ShippingService shippingService) {
        this.shippingService = shippingService;
    }


    public AddressDetails getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressDetails shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public AddressDetails getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(AddressDetails billingAddress) {
        this.billingAddress = billingAddress;
    }

}


