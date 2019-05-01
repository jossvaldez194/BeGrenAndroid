package com.hakagamesstudio.begreen.views.activities;

import android.content.Intent;
import android.os.Bundle;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.category_model.CategoryData;
import com.hakagamesstudio.begreen.pojos.device_model.AppSettingsData;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.support.MyAppPrefsManager;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private MyAppPrefsManager myAppPrefsManager;
    private CallBackRetrofit mCall = ApiUtils.getAPIService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        myAppPrefsManager = new MyAppPrefsManager(this);

        Call<CategoryData> detailCall = mCall.getAllCategories(1);
        detailCall.enqueue(new Callback<CategoryData>() {
            @Override
            public void onResponse(@NonNull Call<CategoryData> call, @NonNull Response<CategoryData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getSuccess().isEmpty()) {
                            BeGreenSupport.getInstance().setCategoriesList(response.body().getData());
                            RequestAppSetting();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryData> call, @NonNull Throwable t) {

            }
        });
    }


    private void RequestAppSetting() {
        Call<AppSettingsData> call = mCall.getAppSetting();
        call.enqueue(new Callback<AppSettingsData>() {
            @Override
            public void onResponse(Call<AppSettingsData> call, Response<AppSettingsData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        BeGreenSupport.getInstance().setAppSettingsDetails(response.body().getProductData().get(0));
                        startActivity(myAppPrefsManager.isFirstTimeLaunch() ? new Intent(SplashActivity.this, IntroActivity.class)
                                : new Intent(SplashActivity.this, Menu_NavDrawer_Acititty.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<AppSettingsData> call, Throwable t) {

            }
        });
    }
}
