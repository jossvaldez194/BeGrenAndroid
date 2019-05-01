package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.customs.EndlessRecyclerViewScroll;
import com.hakagamesstudio.begreen.customs.Utilities;
import com.hakagamesstudio.begreen.databases.User_Cart_DB;
import com.hakagamesstudio.begreen.pojos.cart_model.CartProduct;
import com.hakagamesstudio.begreen.pojos.cart_model.CartProductAttributes;
import com.hakagamesstudio.begreen.pojos.news_model.all_news.NewsData;
import com.hakagamesstudio.begreen.pojos.news_model.all_news.NewsDetails;
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
import com.hakagamesstudio.begreen.views.adapters.listOrchardSingleProduct;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;

public class SingleProductFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.btnLessProducts)
    ImageButton btnLessProducts;
    @BindView(R.id.btnPiece)
    Button btnPiece;
    @BindView(R.id.btnKilogramo)
    Button btnKiloframo;
    @BindView(R.id.imgImageAddToCar)
    ImageView imgAddToCar;
    @BindView(R.id.imgImageProduct)
    ImageView imgProduct;
    @BindView(R.id.txtQunatityProducts)
    TextView txtQuantity;
    @BindView(R.id.txtTotal)
    TextView txtTotal;
    @BindView(R.id.txtSingleNameProduct)
    TextView txtSingleNameProduct;
    @BindView(R.id.btnDetails)
    Button btnDetailsInflate;
    @BindView(R.id.btnOrchards)
    Button btnOrchards;

    private static final String ARG_PARAM1 = "Singgle";
    private CallBackRetrofit mCall = ApiUtils.getAPIService();
    private boolean isFavorite = false;
    private ProductDetails mSingleProduct;
    private String customerID;
    private List<NewsDetails> newsList = new ArrayList<>();
    private Unbinder mUnbinder;
    private Context mContext;
    private View rootView;
    private MyAppPrefsManager myAppPrefsManager;
    private String mComents = "";

    private OnSingleProductListener mListener;
    private ProgressBar mProgressBar;
    private RecyclerView rvListOrchard;
    private listOrchardSingleProduct adapter;
    private MaterialDialog mDialogOrchardSel;

    private boolean isPiece = true;

    public SingleProductFragment() {

    }

    SingleProductFragment newInstance(ProductDetails data) {
        SingleProductFragment fragment = new SingleProductFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getContext() != null) mContext = getContext();
        if (getArguments() != null)
            mSingleProduct = (ProductDetails) getArguments().getSerializable(ARG_PARAM1);
        myAppPrefsManager = new MyAppPrefsManager(mContext);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_single_product, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        imgAddToCar.bringToFront();
        mComents = "";

        if (getActivity() != null) {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            AutoCompleteTextView search = toolbar.findViewById(R.id.searchEditText);
            search.setVisibility(View.GONE);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("");
            }
        }

        if (mSingleProduct != null) {
            txtSingleNameProduct.setText(mSingleProduct.getProductsName());
            txtQuantity.setText("1");
            txtTotal.setText(BeGreenSupport.getInstance().moneyFormatWithOutSimbol(Float.parseFloat(mSingleProduct.getProductsPrice())));
            Glide.with(mContext)
                    .load(com.hakagamesstudio.begreen.support.ConstantValues.ECOMMERCE_URL + mSingleProduct.getProductsImage())
                    .into(imgProduct);

            isFavorite = mSingleProduct.getIsLiked().equalsIgnoreCase("1");
        }
        customerID = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userID", "");
        return rootView;
    }

    @OnClick({R.id.btnAddProducts, R.id.btnLessProducts, R.id.btnPiece, R.id.btnKilogramo, R.id.btnDetails, R.id.btnOrchards, R.id.btnAddToCar})
    void events(View view) {
        int quantity = Integer.parseInt(txtQuantity.getText().toString().trim());
        switch (view.getId()) {
            case R.id.btnAddProducts:
                if (quantity >= 0) {
                    txtQuantity.setText(String.valueOf(Integer.parseInt(txtQuantity.getText().toString()) + 1));
                    float total = Float.parseFloat(txtQuantity.getText().toString()) * Float.parseFloat(mSingleProduct.getProductsPrice());
                    txtTotal.setText(BeGreenSupport.getInstance().moneyFormatWithOutSimbol(total));
                }
                break;
            case R.id.btnLessProducts:
                if (quantity > 0) {
                    btnLessProducts.setEnabled(true);
                    txtQuantity.setText(String.valueOf(Integer.parseInt(txtQuantity.getText().toString().trim()) - 1));
                    float total = Float.parseFloat(txtQuantity.getText().toString().trim()) * Float.parseFloat(mSingleProduct.getProductsPrice().trim());
                    txtTotal.setText(BeGreenSupport.getInstance().moneyFormatWithOutSimbol(total));

                }
                break;
            case R.id.btnPiece:
                btnKiloframo.setBackgroundColor(Color.TRANSPARENT);
                btnPiece.setBackgroundResource(R.mipmap.btn_unidad);
                isPiece = true;
                break;
            case R.id.btnKilogramo:
                btnPiece.setBackgroundColor(Color.TRANSPARENT);
                btnKiloframo.setBackgroundResource(R.mipmap.btn_unidad);
                isPiece = false;
                break;
            case R.id.btnDetails:
                showDialogDescription();
                break;
            case R.id.btnOrchards:
                newsList.clear();
                showDialogListOrchards();
                break;
            case R.id.btnAddToCar:
                if (myAppPrefsManager.isUserLoggedIn()) {
                    if (mSingleProduct.getProductsQuantity() > 0) {
                        if (!txtQuantity.getText().toString().equals("0")) {
                            mSingleProduct.setComentProduct(mComents);
                            addProductToCart(mSingleProduct);
                            User_Cart_DB user_cart_db = new User_Cart_DB();
                            List<CartProduct> cartItemsList = user_cart_db.getCartItems();
                            if (0 < cartItemsList.size()) {
                                mListener.onAddBadge(String.valueOf(cartItemsList.size()));
                                Toast.makeText(mContext, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                                changedFragment();
                            }
                        } else {
                            Toast.makeText(mContext, "No se ha especificado una cantidad para agregar al carrito", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
        }
    }

    private void changedFragment() {
        if (getActivity() != null) {
            Fragment selected = new ProductsFragment().newInstance("Single");
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.addToBackStack(null);
            transaction.replace(R.id.contentLayout, selected);
            transaction.commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void RequestAllNews(int pageNumber) {
        Call<NewsData> call = mCall.getAllNews(1, pageNumber, 0, null);
        call.enqueue(new Callback<NewsData>() {
            @Override
            public void onResponse(@NonNull Call<NewsData> call, @NonNull retrofit2.Response<NewsData> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            // Products have been returned. Add Products to the ProductsList
                            addNews(response.body());

                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            addNews(response.body());
                            Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            // Unable to get Success status
                            Toast.makeText(mContext, "Imposible obtener datos de huertas", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<NewsData> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addNews(NewsData newsData) {
        newsList.addAll(newsData.getNewsData());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorite_product, menu);
        MenuItem bedMenuItem = menu.findItem(R.id.menu_favorite);
        if (isFavorite) {
            bedMenuItem.setIcon(R.drawable.ic_favorite_white_24dp);
        } else {
            bedMenuItem.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                if (isFavorite) {
                    isFavorite = false;
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    setChangeFavorite();
                } else {
                    if (myAppPrefsManager.isUserLoggedIn()) {
                        isFavorite = true;
                        item.setIcon(R.drawable.ic_favorite_white_24dp);
                    }
                    setChangeFavorite();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setChangeFavorite() {
        if (myAppPrefsManager.isUserLoggedIn()) {
            if (isFavorite) {
                mSingleProduct.setIsLiked("1");
                LikeProduct(mSingleProduct.getProductsId(), customerID, mContext);
            } else {
                mSingleProduct.setIsLiked("0");
                UnlikeProduct(mSingleProduct.getProductsId(), customerID, mContext, rootView);
            }
        } else {
            isFavorite = false;
            Intent i = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(i);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }
        }
    }

    private void LikeProduct(int productID, String customerID, final Context context) {
        Call<ProductData> call = mCall.likeProduct(productID, customerID);
        call.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(@NonNull Call<ProductData> call, @NonNull retrofit2.Response<ProductData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            Toast.makeText(mContext, "Producto agregado a favoritos", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "No se pudo agregar el producto a favoritos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mContext, "No se pudo eliminar el producto de favoritos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
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
        double priceAux = Double.parseDouble(txtTotal.getText().toString());

        product.setCustomersBasketQuantity(Integer.parseInt(txtQuantity.getText().toString()));
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

    private void showDialogDescription() {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.custom_detail_product_order, false)
                .show();

        Button btnSaveDes = dialog.getView().findViewById(R.id.btn_saveDescriptionProduct);
        EditText editDes = dialog.getView().findViewById(R.id.editDescriptionContent);
        ImageButton imgClose = dialog.getView().findViewById(R.id.closeDialogComent);
        editDes.setText(mComents);

        imgClose.setOnClickListener(v -> dialog.dismiss());

        btnSaveDes.setOnClickListener(v -> {
            if (!editDes.getText().toString().isEmpty()) {
                mComents = editDes.getText().toString();
                btnDetailsInflate.setTextColor(getResources().getColor(R.color.colorWhite));
                btnDetailsInflate.setBackgroundResource(R.mipmap.ic_producto_detalles_add);
                dialog.dismiss();
            } else {
                Toast.makeText(mContext, "No ha ingresado ningun detalle", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void showDialogListOrchards() {
         mDialogOrchardSel = new MaterialDialog.Builder(mContext)
                .customView(R.layout.content_list_orchards_single_product, false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();

        ProgressBar progressBar = mDialogOrchardSel.getView().findViewById(R.id.loading_bar_singleProduct);
        rvListOrchard = mDialogOrchardSel.getView().findViewById(R.id.rvListOrchards_singleproduct);
        ImageButton close = mDialogOrchardSel.getView().findViewById(R.id.img_close_orchards_list_dialog);
        mProgressBar = progressBar;
        int pageNo = 0;
        RequestAllNews(pageNo);

        close.setOnClickListener(v -> mDialogOrchardSel.dismiss());
        adapter = new listOrchardSingleProduct(newsList, this, mContext);
        rvListOrchard.setLayoutManager(new LinearLayoutManager(mContext));
        rvListOrchard.setAdapter(adapter);

        rvListOrchard.addOnScrollListener(new EndlessRecyclerViewScroll() {
            // Override abstract method onLoadMore() of EndlessRecyclerViewScroll class
            @Override
            public void onLoadMore(int current_page) {
                progressBar.setVisibility(View.VISIBLE);
                // Execute AsyncTask LoadMoreTask to Load More Products from Server
                new LoadMoreTask(current_page).execute();
            }
        });

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSingleProductListener) {
            mListener = (OnSingleProductListener) context;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_orchard_dialog:
                NewsDetails offerts = (NewsDetails) v.getTag();
                MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                        .cancelable(false)
                        .title("Seleccionar huerta")
                        .content("¿Deseas que tu producto sea de: " + offerts.getNewsName() + "?")
                        .canceledOnTouchOutside(false)
                        .negativeText("Cancelar")
                        .positiveText("Aceptar")
                        .onPositive((dialog1, which) -> {
                            btnOrchards.setTextColor(getResources().getColor(R.color.colorWhite));
                            btnOrchards.setBackgroundResource(R.mipmap.ic_producto_detalles_add);
                            dialog1.dismiss();
                            mDialogOrchardSel.dismiss();
                        })
                        .onNegative((dialog12, which) -> dialog12.dismiss())
                        .show();
                break;
        }
    }

    public interface OnSingleProductListener {
        void onAddBadge(String value);
    }

    private class LoadMoreTask extends AsyncTask<String, Void, String> {

        int page_number;

        private LoadMoreTask(int page_number) {
            this.page_number = page_number;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestAllNews(page_number);
            return "All Done!";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


}
