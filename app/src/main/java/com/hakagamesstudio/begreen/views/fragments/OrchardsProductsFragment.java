package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.customs.EndlessRecyclerViewScroll;
import com.hakagamesstudio.begreen.pojos.OrchardsProductSingle;
import com.hakagamesstudio.begreen.pojos.product_model.GetAllProducts;
import com.hakagamesstudio.begreen.pojos.product_model.ProductData;
import com.hakagamesstudio.begreen.pojos.product_model.ProductDetails;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.views.adapters.OrchardProductAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;

public class OrchardsProductsFragment extends Fragment {
    @BindView(R.id.all_products_recycler)
    RecyclerView all_products_recycler;
    @BindView(R.id.loading_bar)
    ProgressBar progressBar;

    private static final String ARG_PARAM1 = "param1";

    private View rootView;
    private Context mContex;
    private String customerID;
    private String sortBy = "a to z";
    private String mNameOrchard = "";
    private List<OrchardsProductSingle> filterListDetail = new ArrayList<>();
    private OrchardProductAdapter productAdapter;
    private List<ProductDetails> productsList;
    private Unbinder mUnbinder;
    private CallBackRetrofit mCall = ApiUtils.getAPIService();
    private Call<ProductData> mGetProduct;

    public OrchardsProductsFragment() {

    }

    public OrchardsProductsFragment newInstance(String param1) {
        OrchardsProductsFragment fragment = new OrchardsProductsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) mContex = getContext();
        if (getArguments() != null) {
            mNameOrchard = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_orchards_products, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        customerID = mContex.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userID", "");

        productsList = new ArrayList<>();

        int pageNo = 0;
        RequestAllProducts(pageNo, sortBy);

        productAdapter = new OrchardProductAdapter(filterListDetail);
        setRecyclerViewLayoutManager();
        all_products_recycler.setAdapter(productAdapter);
        all_products_recycler.addOnScrollListener(new EndlessRecyclerViewScroll() {
            // Override abstract method onLoadMore() of EndlessRecyclerViewScroll class
            @Override
            public void onLoadMore(int current_page) {
                progressBar.setVisibility(View.VISIBLE);
                // Execute AsyncTask LoadMoreTask to Load More Products from Server
                new LoadMoreTask(current_page).execute();
            }
        });

        productAdapter.notifyDataSetChanged();
        return rootView;
    }

    private void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        if (all_products_recycler.getLayoutManager() != null)
            scrollPosition = ((LinearLayoutManager) all_products_recycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        all_products_recycler.setLayoutManager(new LinearLayoutManager(mContex));
        all_products_recycler.setAdapter(productAdapter);
        all_products_recycler.scrollToPosition(scrollPosition);
    }

    private void addProducts(ProductData productData) {
        List<OrchardsProductSingle> list = new ArrayList<>();
        productsList.addAll(productData.getProductData());
        for (int i = 0; i < productsList.size(); i++) {
            if (productsList.get(i).getManufacturersName() != null)
                if (productsList.get(i).getManufacturersName().toLowerCase().contains(mNameOrchard.toLowerCase())) {
                    list.add(new OrchardsProductSingle(OrchardsProductSingle.CATEGORY,
                            productsList.get(i).getCategoriesName(), ""));
                    list.add(new OrchardsProductSingle(OrchardsProductSingle.PRODUCT,
                            "", productsList.get(i).getProductsName()));
                }
        }

        filterListDetail = removeDuplicates(list);
        productAdapter.notifyDataSetChanged();
    }

    private static <T> List<T> removeDuplicates(List<T> list) {
        Set<T> set = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    private void RequestAllProducts(int pageNumber, String sortBy) {
        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setPageNumber(pageNumber);
        getAllProducts.setLanguageId(1);
        getAllProducts.setCustomersId(customerID);
        getAllProducts.setType(sortBy);

        mGetProduct = mCall.getAllProducts(getAllProducts);
        mGetProduct.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(@NonNull Call<ProductData> call, @NonNull retrofit2.Response<ProductData> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            // Products have been returned. Add Products to the ProductsList
                            addProducts(response.body());

                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            // Products haven't been returned. Call the method to process some implementations
                            addProducts(response.body());
                            Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();

                        } else {
                            Snackbar.make(rootView, "Imposible obtener datos de huertas", Snackbar.LENGTH_SHORT).show();
                        }
                        if (isAdded()) {
                            if (progressBar != null)
                                progressBar.setVisibility(View.GONE);
                        }
                    } else
                        Toast.makeText(getContext(), "" + response.message(), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "" + response.message(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ProductData> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGetProduct.cancel();
        mUnbinder.unbind();
    }

    private class LoadMoreTask extends AsyncTask<String, Void, String> {
        int page_number;

        private LoadMoreTask(int page_number) {
            this.page_number = page_number;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestAllProducts(page_number, sortBy);
            return "All Done!";
        }
    }
}
