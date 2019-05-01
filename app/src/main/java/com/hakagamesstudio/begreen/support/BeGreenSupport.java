package com.hakagamesstudio.begreen.support;

import android.content.Context;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.category_model.CategoryData;
import com.hakagamesstudio.begreen.pojos.category_model.CategoryDetails;
import com.hakagamesstudio.begreen.pojos.device_model.AppSettingsDetails;
import com.hakagamesstudio.begreen.pojos.news_model.all_news.NewsDetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class BeGreenSupport {
    private static BeGreenSupport mInstance = new BeGreenSupport();

    private String nameOrchards;
    private ConstraintLayout mContentrButtonsOrchards;
    private CategoryData mCategoryData;
    private List<CategoryDetails> categoriesList = new ArrayList<>();
    private List<CategoryDetails> subCategoryList = new ArrayList<>();
    private NewsDetails newsDetails;
    private AppSettingsDetails appSettingsDetails = null;

    public static BeGreenSupport getInstance() {
        if (mInstance == null) {
            mInstance = new BeGreenSupport();
        }

        return mInstance;
    }

    private BeGreenSupport() {
    }

    public MaterialDialog createProgressDialog(String message, Context context) {
        return new MaterialDialog.Builder(context)
                .content(message)
                .cancelable(false)
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .progressIndeterminateStyle(false)
                .show();
    }

    public String moneyFormat(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
        String newValue;
        if (value != 0) {
            newValue = decimalFormat.format(value);
        } else {
            return "$0";
        }
        return "$" + newValue;
    }

    public String moneyFormatWithOutSimbol(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
        String newValue;
        if (value != 0) {
            newValue = decimalFormat.format(value);
        } else {
            return "0";
        }
        return newValue;
    }

    public MaterialDialog showDialogInternet(String message, boolean isConnected, Context context) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.content_select_orchard, false)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();

        RecyclerView recyclerView = dialog.getView().findViewById(R.id.recyclerView);
        Button button = dialog.getView().findViewById(R.id.btn_save_Orchard);

        //MenuCategoryAdapter adapter = new MenuCategoryAdapter(mFilterListCategory, this, mContext);
        //recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        //recyclerView.setAdapter(adapter);

        return dialog;
    }

    public CategoryData getmCategoryData() {
        return mCategoryData;
    }

    public void setmCategoryData(CategoryData mCategoryData) {
        this.mCategoryData = mCategoryData;
    }

    public List<CategoryDetails> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(List<CategoryDetails> categoriesList) {
        this.categoriesList = categoriesList;
    }

    public List<CategoryDetails> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(List<CategoryDetails> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }

    public String getNameOrchards() {
        return nameOrchards;
    }

    public void setNameOrchards(String nameOrchards) {
        this.nameOrchards = nameOrchards;
    }

    public ConstraintLayout getmContentrButtonsOrchards() {
        return mContentrButtonsOrchards;
    }

    public void setmContentrButtonsOrchards(ConstraintLayout mContentrButtonsOrchards) {
        this.mContentrButtonsOrchards = mContentrButtonsOrchards;
    }

    public NewsDetails getNewsDetails() {
        return newsDetails;
    }

    public void setNewsDetails(NewsDetails newsDetails) {
        this.newsDetails = newsDetails;
    }

    public AppSettingsDetails getAppSettingsDetails() {
        return appSettingsDetails;
    }

    public void setAppSettingsDetails(AppSettingsDetails appSettingsDetails) {
        this.appSettingsDetails = appSettingsDetails;
    }
}
