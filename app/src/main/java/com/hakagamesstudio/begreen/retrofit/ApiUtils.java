package com.hakagamesstudio.begreen.retrofit;

public class ApiUtils {

    private ApiUtils() {
    }

    public static CallBackRetrofit getAPIService() {
        return ApiClient.getClient().create(CallBackRetrofit.class);
    }
}
