package com.hakagamesstudio.begreen.CallBacks;


import com.hakagamesstudio.begreen.pojos.product_model.ProductData;

public interface ProductsCallBack {

    interface view{
        void onSuccessRequest(ProductData datasource);

        void onEmptyProducts(String message);
    }

    interface presenter{
        void onSendRequestProduct();
    }
}
