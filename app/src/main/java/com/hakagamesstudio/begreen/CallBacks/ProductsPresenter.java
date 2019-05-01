package com.hakagamesstudio.begreen.CallBacks;

import com.hakagamesstudio.begreen.pojos.category_model.CategoryDetails;
import com.hakagamesstudio.begreen.pojos.product_model.GetAllProducts;
import com.hakagamesstudio.begreen.pojos.product_model.ProductData;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.support.BeGreenSupport;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsPresenter implements ProductsCallBack.presenter {

    private ProductsCallBack.view mProductView;
    private CallBackRetrofit mCall = ApiUtils.getAPIService();

    public ProductsPresenter(ProductsCallBack.view mProductView) {
        this.mProductView = mProductView;
    }

    @Override
    public void onSendRequestProduct() {
        List<CategoryDetails> subCategoryList = BeGreenSupport.getInstance().getSubCategoryList();

        if (subCategoryList.size() > 0) {
            for (int i = 0; i < subCategoryList.size(); i++) {
                GetAllProducts getAllProducts = new GetAllProducts();
                getAllProducts.setPageNumber(0);
                getAllProducts.setLanguageId(1);
                getAllProducts.setCategoriesId(subCategoryList.get(i).getId());
                getAllProducts.setType("a to z");

                Call<ProductData> callProducts = mCall.getAllProducts(getAllProducts);
                callProducts.enqueue(new Callback<ProductData>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductData> call, @NonNull Response<ProductData> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                mProductView.onSuccessRequest(response.body());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductData> call, @NonNull Throwable t) {

                    }
                });
            }
        }else{
            mProductView.onEmptyProducts("No se encontraron productos relacionados");
        }

    }
}
