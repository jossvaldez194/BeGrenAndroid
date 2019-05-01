package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.customs.DialogLoader;
import com.hakagamesstudio.begreen.customs.EndlessRecyclerViewScroll;
import com.hakagamesstudio.begreen.customs.Utilities;
import com.hakagamesstudio.begreen.databases.User_Cart_DB;
import com.hakagamesstudio.begreen.pojos.cart_model.CartProduct;
import com.hakagamesstudio.begreen.pojos.cart_model.CartProductAttributes;
import com.hakagamesstudio.begreen.pojos.product_model.GetAllProducts;
import com.hakagamesstudio.begreen.pojos.product_model.Option;
import com.hakagamesstudio.begreen.pojos.product_model.ProductData;
import com.hakagamesstudio.begreen.pojos.product_model.ProductDetails;
import com.hakagamesstudio.begreen.pojos.product_model.Value;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.support.MyAppPrefsManager;
import com.hakagamesstudio.begreen.views.activities.LoginActivity;
import com.hakagamesstudio.begreen.views.activities.Menu_NavDrawer_Acititty;
import com.hakagamesstudio.begreen.views.adapters.WishListAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;

public class WishListFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private CallBackRetrofit mCall = ApiUtils.getAPIService();
    private String customerID;
    private WishListAdapter adapter;
    private View rootView;
    private ProgressBar progressBar;
    private ConstraintLayout mContentButtons;
    private Fragment fragment = null;
    private List<ProductDetails> favouriteProductsList;
    private DialogLoader dialogLoader;
    private MyAppPrefsManager myAppPrefsManager;

    private OnWhishListListener mListener;
    public WishListFragment() {
    }


    public static WishListFragment newInstance() {
        WishListFragment fragment = new WishListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) mContext = getContext();
        mContentButtons = BeGreenSupport.getInstance().getmContentrButtonsOrchards();
        mContentButtons.setVisibility(View.VISIBLE);
        Menu_NavDrawer_Acititty.imgHome.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wish_list, container, false);
        myAppPrefsManager = new MyAppPrefsManager(mContext);
        if (myAppPrefsManager.isUserLoggedIn()) {

            RecyclerView favourites_recycler = rootView.findViewById(R.id.products_recycler);
            progressBar = rootView.findViewById(R.id.loading_bar);

            if (getActivity() != null) {
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                AutoCompleteTextView search = toolbar.findViewById(R.id.searchEditText);
                search.setVisibility(View.GONE);
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle("Tus favoritos");
                }
            }

            progressBar.setVisibility(View.GONE);
            favouriteProductsList = new ArrayList<>();
            dialogLoader = new DialogLoader(getContext());
            dialogLoader.showProgressDialog();

            customerID = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userID", "");
            RequestWishList(0);

            adapter = new WishListAdapter(favouriteProductsList, mContext, this);
            // Set the Adapter and LayoutManager to the RecyclerView
            favourites_recycler.setAdapter(adapter);
            favourites_recycler.setLayoutManager(new LinearLayoutManager(mContext));


            // Handle Scroll event of the RecyclerView
            favourites_recycler.addOnScrollListener(new EndlessRecyclerViewScroll() {
                @Override
                public void onLoadMore(int current_page) {
                    progressBar.setVisibility(View.VISIBLE);

                    // Execute LoadMoreTask
                    new LoadMoreTask(current_page).execute();
                }

            });
        } else {
            changedFragment(R.mipmap.app_favoritos, "No tienes favoritos :(");
        }

        Menu_NavDrawer_Acititty.imgMap.setBackgroundResource(R.mipmap.huertas_boton_prod);
        Menu_NavDrawer_Acititty.imgMap.setTextColor(getResources().getColor(R.color.colorBlack));
        Menu_NavDrawer_Acititty.imgProducts.setBackgroundResource(R.mipmap.huertas_select);
        Menu_NavDrawer_Acititty.imgProducts.setTextColor(getResources().getColor(R.color.colorWhite));
        Menu_NavDrawer_Acititty.imgProducts.setText("Productos");
        Menu_NavDrawer_Acititty.imgMap.setText("Huertas");

        Menu_NavDrawer_Acititty.imgMap.setOnClickListener(v -> {
            Menu_NavDrawer_Acititty.imgMap.setTextColor(getResources().getColor(R.color.colorWhite));
            Menu_NavDrawer_Acititty.imgMap.setText("Huertas");
            Menu_NavDrawer_Acititty.imgProducts.setTextColor(getResources().getColor(R.color.colorBlack));
            Menu_NavDrawer_Acititty.imgMap.setBackgroundResource(R.mipmap.huertas_select);
            Menu_NavDrawer_Acititty.imgProducts.setBackgroundResource(R.mipmap.huertas_boton_prod);
            fragment = new WishListOrchardsFragment().newInstance();
            changedFragment2(fragment);
        });

        Menu_NavDrawer_Acititty.imgProducts.setOnClickListener(v -> {
            Menu_NavDrawer_Acititty.imgMap.setBackgroundResource(R.mipmap.huertas_boton_prod);
            Menu_NavDrawer_Acititty.imgMap.setTextColor(getResources().getColor(R.color.colorBlack));
            Menu_NavDrawer_Acititty.imgProducts.setBackgroundResource(R.mipmap.huertas_select);
            Menu_NavDrawer_Acititty.imgProducts.setTextColor(getResources().getColor(R.color.colorWhite));
            Menu_NavDrawer_Acititty.imgProducts.setText("Productos");
            fragment = new WishListFragment().newInstance();
            changedFragment2(fragment);
        });

        return rootView;
    }

    private void changedFragment2(Fragment selected) {
        if (getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.contentLayout, selected);
            transaction.commit();
        }
    }

    private void changedFragment(int Image, String Description) {
        if (getActivity() != null) {
            Fragment selected = new WithOutProductsFragment().newInstance(Image, Description);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.contentLayout, selected);
            transaction.commit();
        }
    }

    private void addProducts(ProductData productData) {
        favouriteProductsList.addAll(productData.getProductData());
        if (0<favouriteProductsList.size()) {
            adapter.notifyDataSetChanged();
        }else {
            changedFragment(R.mipmap.empty_set, "Aún no has agregado artículos a tu lista de deseos");
        }
    }

    private void RequestWishList(int pageNumber) {
        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setPageNumber(pageNumber);
        getAllProducts.setLanguageId(1);
        getAllProducts.setCustomersId(customerID);
        getAllProducts.setType("wishlist");


        Call<ProductData> call = mCall.getAllProducts(getAllProducts);
        call.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(@NonNull Call<ProductData> call, @NonNull retrofit2.Response<ProductData> response) {
                if (dialogLoader != null)
                    dialogLoader.hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            addProducts(response.body());
                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            addProducts(response.body());
                            Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);

                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductData> call, @NonNull Throwable t) {
                if (isAdded()) {
                    dialogLoader.hideProgressDialog();
                    changedFragment(R.mipmap.no_signal, "Ocurrió un error en la red");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        ProductDetails data = (ProductDetails) view.getTag();
        int index = 0;
        switch (view.getId()) {
            case R.id.imgDeleteWish:
                for (int i = 0; i < favouriteProductsList.size(); i++) {
                    if (data.getProductsId() == favouriteProductsList.get(i).getProductsId()) {
                        index = i;
                        break;
                    }
                }
                UnlikeProduct(data.getProductsId(), customerID, mContext, rootView);
                adapter.removeAt(index);
                break;

            case R.id.imgAddFromWish:
                if (myAppPrefsManager.isUserLoggedIn()) {
                    addProductToCart(data);
                    User_Cart_DB user_cart_db = new User_Cart_DB();
                    List<CartProduct> cartItemsList = user_cart_db.getCartItems();
                    if (0 < cartItemsList.size()) {
                        mListener.onAddBadge(String.valueOf(cartItemsList.size()));
                        Toast.makeText(mContext, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                }

                /*for (int i = 0; i < favouriteProductsList.size(); i++) {
                    if (data.getProductsId() == favouriteProductsList.get(i).getProductsId()) {
                        index = i;
                        break;
                    }
                }

                Toast.makeText(mContext, String.valueOf(index), Toast.LENGTH_SHORT).show();*/
                break;
        }

    }

    private class LoadMoreTask extends AsyncTask<String, Void, String> {
        int page_number;

        private LoadMoreTask(int page_number) {
            this.page_number = page_number;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestWishList(page_number);
            return "All Done!";
        }
    }

    private void UnlikeProduct(int productID, String customerID, final Context context, final View view) {
        Call<ProductData> call = mCall.unlikeProduct(productID, customerID);
        call.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(@NonNull Call<ProductData> call, @NonNull retrofit2.Response<ProductData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            Toast.makeText(mContext, "Producto eliminado de favoritos", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, context.getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductData> call, @NonNull Throwable t) {
                Toast.makeText(context, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addProductToCart(ProductDetails product) {
        CartProduct cartProduct = new CartProduct();

        double productBasePrice, productFinalPrice, attributesPrice = 0;
        List<CartProductAttributes> selectedAttributesList = new ArrayList<>();
        final String discount = Utilities.checkDiscount(product.getProductsPrice(), product.getDiscountPrice());

        // Get Product's Price based on Discount
        if (discount != null) {
            product.setIsSaleProduct("1");
            productBasePrice = Double.parseDouble(product.getDiscountPrice());
        } else {
            product.setIsSaleProduct("0");
            productBasePrice = Double.parseDouble(product.getProductsPrice());
        }

        // Get Default Attributes from AttributesList
        for (int i = 0; i < product.getAttributes().size(); i++) {
            CartProductAttributes productAttribute = new CartProductAttributes();
            // Get Name and First Value of current Attribute
            Option option = product.getAttributes().get(i).getOption();
            Value value = product.getAttributes().get(i).getValues().get(0);
            // Add the Attribute's Value Price to the attributePrices
            String attrPrice = value.getPricePrefix() + value.getPrice();
            attributesPrice += Double.parseDouble(attrPrice);
            // Add Value to new List
            List<Value> valuesList = new ArrayList<>();
            valuesList.add(value);
            // Set the Name and Value of Attribute
            productAttribute.setOption(option);
            productAttribute.setValues(valuesList);
            // Add current Attribute to selectedAttributesList
            selectedAttributesList.add(i, productAttribute);
        }

        // Add Attributes Price to Product's Final Price
        productFinalPrice = productBasePrice + attributesPrice;
        double priceAux = Double.parseDouble(product.getProductsPrice().trim());

        product.setCustomersBasketQuantity(Integer.parseInt("1"));
        product.setProductsPrice(String.valueOf(productBasePrice));
        product.setAttributesPrice(String.valueOf(attributesPrice));
        product.setProductsFinalPrice(String.valueOf(productFinalPrice));
        product.setTotalPrice(String.valueOf(priceAux));
        cartProduct.setCustomersBasketProduct(product);
        cartProduct.setCustomersBasketProductAttributes(selectedAttributesList);
        AddCartItem(cartProduct);
        ((Menu_NavDrawer_Acititty) mContext).invalidateOptionsMenu();

    }

    private void AddCartItem(CartProduct cartProduct) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.addCartItem(cartProduct);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnWhishListListener) {
            mListener = (OnWhishListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnWhishListListener {
        void onAddBadge(String value);
    }
}