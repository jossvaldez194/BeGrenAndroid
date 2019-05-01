package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hakagamesstudio.begreen.CallBacks.CartListener;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.databases.User_Cart_DB;
import com.hakagamesstudio.begreen.databases.User_Info_DB;
import com.hakagamesstudio.begreen.pojos.cart_model.CartProduct;
import com.hakagamesstudio.begreen.pojos.coupons_model.CouponsData;
import com.hakagamesstudio.begreen.pojos.coupons_model.CouponsInfo;
import com.hakagamesstudio.begreen.pojos.product_model.ProductDetails;
import com.hakagamesstudio.begreen.pojos.shipping_model.PostTaxAndShippingData;
import com.hakagamesstudio.begreen.pojos.shipping_model.ShippingRateData;
import com.hakagamesstudio.begreen.pojos.shipping_model.ShippingRateDetails;
import com.hakagamesstudio.begreen.pojos.shipping_model.ShippingService;
import com.hakagamesstudio.begreen.pojos.user_model.UserDetails;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.support.MyAppPrefsManager;
import com.hakagamesstudio.begreen.views.activities.LoginActivity;
import com.hakagamesstudio.begreen.views.activities.Menu_NavDrawer_Acititty;
import com.hakagamesstudio.begreen.views.adapters.CartItemsAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class MyCartFragment extends Fragment implements CartListener {

    @BindView(R.id.recyclerMyCart)
    RecyclerView cart_items_recycler;
    @BindView(R.id.txtEnvio)
    TextView txtEnvio;
    @BindView(R.id.txtErrorCupon)
    TextView txtErrorCupon;
    @BindView(R.id.editCupon)
    EditText editCupon;
    @BindView(R.id.btn_hacer_pedido)
    Button btnHacerPedido;

    public static TextView cart_total_price;
    public static TextView txtSubtotal;
    private View rootView;
    private boolean isTextClean = true;
    private ShippingService shippingService;
    private CallBackRetrofit mCallToServer;
    private User_Cart_DB user_cart_db = new User_Cart_DB();
    private List<CartProduct> cartItemsList = new ArrayList<>();
    private List<CouponsInfo> couponsList = new ArrayList<>();
    public static Context mContext;
    private UserDetails userInfo;
    private User_Info_DB userInfoDB = new User_Info_DB();
    private List<ShippingService> localPickupServicesList = new ArrayList<>();
    private List<ShippingService> freeShippingServicesList = new ArrayList<>();
    private List<ShippingService> flatRateServicesList = new ArrayList<>();
    private List<ShippingService> upsShippingServicesList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public MyCartFragment() {
    }

    public MyCartFragment newInstance() {
        MyCartFragment fragment = new MyCartFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getContext() != null) mContext = getContext();
        cartItemsList = user_cart_db.getCartItems();
        mCallToServer = ApiUtils.getAPIService();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_cart, container, false);
        ButterKnife.bind(this, rootView);
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(mContext);
        if (myAppPrefsManager.isUserLoggedIn()) {
            if (0 < cartItemsList.size()) {

                cart_total_price = rootView.findViewById(R.id.txtTotal_mycart_fragment);
                txtSubtotal = rootView.findViewById(R.id.txtSubtotal);

                if (getActivity() != null) {
                    Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                    AutoCompleteTextView search = toolbar.findViewById(R.id.searchEditText);
                    search.setVisibility(View.GONE);
                    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle("Tu cesta de compra");
                    }
                }

                RequestShippingRates();

                btnHacerPedido.setOnClickListener(view -> {
                    // Check if cartItemsList isn't empty
                    if (cartItemsList.size() != 0) {
                        // Check if User is Logged-In
                        if (myAppPrefsManager.isUserLoggedIn()) {
                            Bundle bundle = new Bundle();
                            bundle.putString("subTotal", txtSubtotal.getText().toString());
                            bundle.putString("envio", txtEnvio.getText().toString());
                            bundle.putString("cupon", editCupon.getText().toString());
                            bundle.putString("total", cart_total_price.getText().toString());

                            Fragment fragment = new CheckOutFragment();
                            fragment.setArguments(bundle);
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.contentLayout, fragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(getString(R.string.actionHome)).commit();
                        } else {
                            // Navigate to Login Activity
                            Intent i = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(i);
                            ((Menu_NavDrawer_Acititty) mContext).finish();
                            ((Menu_NavDrawer_Acititty) mContext).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                        }
                    }
                });

                editCupon.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().isEmpty()) {
                            isTextClean = true;
                            txtErrorCupon.setVisibility(View.GONE);
                        } else {
                            isTextClean = false;
                        }
                        GetCouponInfo(editCupon.getText().toString());
                    }
                });

            } else {
                changedFragment();
            }
        } else {
            changedFragment();
        }


        return rootView;
    }

    private void changedFragment() {
        if (getActivity() != null) {
            Fragment selected = new WithOutProductsFragment().newInstance(R.mipmap.empty_set, "Aún no has agregado artículos a tu carrito");
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.contentLayout, selected);
            transaction.commit();
        }
    }

    //*********** Static method to Add the given Item to User's Cart ********//
    public static void AddCartItem(CartProduct cartProduct) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.addCartItem(cartProduct);
    }

    //*********** Static method to Get the Cart Product based on product_id ********//
    public static CartProduct GetCartProduct(int product_id) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        return user_cart_db.getCartProduct(product_id);
    }

    //*********** Static method to Clear User's Cart ********//
    public static void ClearCart() {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.clearCart();
    }

    //*********** Static method to Update the given Item in User's Cart ********//

    public static void UpdateCartItem(CartProduct cartProduct) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.updateCartItem(cartProduct);
    }

    //*********** Static method to get total number of Items in User's Cart ********//

    public static int getCartSize() {
        int cartSize = 0;
        User_Cart_DB user_cart_db = new User_Cart_DB();
        List<CartProduct> cartItems = user_cart_db.getCartItems();
        for (int i = 0; i < cartItems.size(); i++) {
            cartSize += cartItems.get(i).getCustomersBasketProduct().getCustomersBasketQuantity();
        }
        return cartSize;
    }


    //*********** Static method to check if the given Product is already in User's Cart ********//

    public static boolean checkCartHasProduct(int cart_item_id) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        return user_cart_db.getCartItemsIDs().contains(cart_item_id);
    }

    private void GetCouponInfo(String coupon_code) {
        Call<CouponsData> call = mCallToServer.getCouponInfo(coupon_code);
        call.enqueue(new Callback<CouponsData>() {
            @Override
            public void onResponse(@NonNull Call<CouponsData> call, @NonNull retrofit2.Response<CouponsData> response) {
                //dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            final CouponsInfo couponsInfo = response.body().getData().get(0);
                            txtErrorCupon.setVisibility(View.GONE);

                        /*if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_cart")
                                || couponsInfo.getDiscountType().equalsIgnoreCase("percent")) {
                            if (validateCouponCart(couponsInfo))
                                applyCoupon(couponsInfo);

                        } else if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_product")
                                || couponsInfo.getDiscountType().equalsIgnoreCase("percent_product")) {
                            if (validateCouponProduct(couponsInfo))
                                applyCoupon(couponsInfo);
                        } */

                        /*if (couponsList.size() != 0 && couponsInfo.getIndividualUse().equalsIgnoreCase("1")) {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

                            dialog.setTitle(getString(R.string.add_coupon));
                            dialog.setMessage(getString(R.string.coupon_removes_other_coupons));

                            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_cart")
                                            || couponsInfo.getDiscountType().equalsIgnoreCase("percent")) {
                                        if (validateCouponCart(couponsInfo))
                                            applyCoupon(couponsInfo);

                                    } else if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_product")
                                            || couponsInfo.getDiscountType().equalsIgnoreCase("percent_product")) {
                                        if (validateCouponProduct(couponsInfo))
                                            applyCoupon(couponsInfo);
                                    }
                                }
                            });

                            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();

                        } else {
                            if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_cart")
                                    || couponsInfo.getDiscountType().equalsIgnoreCase("percent")) {
                                if (validateCouponCart(couponsInfo))
                                    applyCoupon(couponsInfo);

                            } else if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_product")
                                    || couponsInfo.getDiscountType().equalsIgnoreCase("percent_product"))A {
                                if (validateCouponProduct(couponsInfo))
                                    applyCoupon(couponsInfo);
                            }
                        }*/

                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            if (isTextClean)
                                txtErrorCupon.setVisibility(View.GONE);
                            else
                                txtErrorCupon.setVisibility(View.VISIBLE);
                            txtErrorCupon.setText("Cupón no válido");
                        }

                    } else {
                        // Unexpected Response from Server
                        Toast.makeText(mContext, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CouponsData> call, Throwable t) {
                //dialogLoader.hideProgressDialog();
                Toast.makeText(mContext, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

   /*private boolean validateCouponCart(CouponsInfo coupon) {

        int user_used_this_coupon_counter = 0;

        boolean coupon_already_applied = false;

        boolean valid_user_email_for_coupon = false;
        boolean valid_sale_items_in_for_coupon = true;

        boolean valid_items_in_cart = false;
        boolean valid_category_items_in_cart = false;

        boolean no_excluded_item_in_cart = true;
        boolean no_excluded_category_item_in_cart = true;


        if (couponsList.size() != 0) {
            for (int i=0;  i<couponsList.size();  i++) {
                if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                    coupon_already_applied = true;
                }
            }
        }


        for (int i=0;  i<coupon.getUsedBy().size();  i++) {
            if (userInfo.getCustomersId().equalsIgnoreCase(coupon.getUsedBy().get(i))) {
                user_used_this_coupon_counter += 1;
            }
        }


        if (coupon.getEmailRestrictions().size() != 0) {
            if (isStringExistsInList(userInfo.getCustomersEmailAddress(), coupon.getEmailRestrictions())) {
                valid_user_email_for_coupon = true;
            }
        }
        else {
            valid_user_email_for_coupon = true;
        }



        for (int i=0;  i<checkoutItemsList.size();  i++) {

            int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
            int categoryID = checkoutItemsList.get(i).getCustomersBasketProduct().getCategoriesId();


            if (coupon.getExcludeSaleItems().equalsIgnoreCase("1") && checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")) {
                valid_sale_items_in_for_coupon = false;
            }


            if (coupon.getExcludedProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories())) {
                    no_excluded_category_item_in_cart = false;
                }
            }

            if (coupon.getExcludeProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())) {
                    no_excluded_item_in_cart = false;
                }
            }

            if (coupon.getProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories())) {
                    valid_category_items_in_cart = true;
                }
            } else {
                valid_category_items_in_cart = true;
            }


            if (coupon.getProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())) {
                    valid_items_in_cart = true;
                }
            } else {
                valid_items_in_cart = true;
            }

        }


        /////////////////////////////////////////////////////

        if (!disableOtherCoupons) {
            if (!coupon_already_applied) {
                if (!Utilities.checkIsDatePassed(coupon.getExpiryDate())) {
                    if (Integer.parseInt(coupon.getUsageCount()) <= Integer.parseInt(coupon.getUsageLimit())) {
                        if (user_used_this_coupon_counter <= Integer.parseInt(coupon.getUsageLimitPerUser())) {
                            if (valid_user_email_for_coupon) {
                                if (Double.parseDouble(coupon.getMinimumAmount()) <= checkoutTotal) {
                                    if (Double.parseDouble(coupon.getMaximumAmount()) == 0.0  ||  checkoutTotal <= Double.parseDouble(coupon.getMaximumAmount())) {
                                        if (Integer.parseInt(coupon.getLimitUsageToXItems()) == 0  ||  checkoutItemsList.size() <= Integer.parseInt(coupon.getLimitUsageToXItems())) {
                                            if (valid_sale_items_in_for_coupon) {
                                                if (no_excluded_category_item_in_cart) {
                                                    if (no_excluded_item_in_cart) {
                                                        if (valid_category_items_in_cart) {
                                                            if (valid_items_in_cart) {

                                                                return true;

                                                            } else {
                                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_products));
                                                                return false;
                                                            }
                                                        } else {
                                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_categories));
                                                            return false;
                                                        }
                                                    } else {
                                                        showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_products));
                                                        return false;
                                                    }
                                                } else {
                                                    showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_categories));
                                                    return false;
                                                }
                                            } else {
                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_sale_items));
                                                return false;
                                            }
                                        } else {
                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_too_many_products));
                                            return false;
                                        }
                                    } else {
                                        showSnackBarForCoupon(getString(R.string.coupon_max_amount_is_less_than_order_total));
                                        return false;
                                    }
                                } else {
                                    showSnackBarForCoupon(getString(R.string.coupon_min_amount_is_greater_than_order_total));
                                    return false;
                                }
                            } else {
                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_you));
                                return false;
                            }
                        } else {
                            showSnackBarForCoupon(getString(R.string.coupon_used_by_you));
                            return false;
                        }
                    } else {
                        showSnackBarForCoupon(getString(R.string.coupon_used_by_all));
                        return false;
                    }
                } else {
                    checkout_coupon_code.setError(getString(R.string.coupon_expired));
                    return false;
                }
            } else {
                showSnackBarForCoupon(getString(R.string.coupon_applied));
                return false;
            }
        } else {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_used_with_existing));
            return false;
        }

    }

    public void applyCoupon(CouponsInfo coupon) {

        if (coupon.getIndividualUse().equalsIgnoreCase("1")) {
            couponsList.clear();
            checkoutDiscount = 0.0;
            checkoutShipping = checkoutShippingCost;
            disableOtherCoupons = true;
            setCheckoutTotal();
        }

        if (coupon.getFreeShipping().equalsIgnoreCase("1")) {
            checkoutShipping = 0.0;
        }


        double discount = 0.0;

        if (coupon.getDiscountType().equalsIgnoreCase("fixed_cart")) {
            discount = Double.parseDouble(coupon.getAmount());

        }
        else if (coupon.getDiscountType().equalsIgnoreCase("percent")) {
            discount = (checkoutSubtotal * Double.parseDouble(coupon.getAmount())) / 100;

        }
        else if (coupon.getDiscountType().equalsIgnoreCase("fixed_product")) {

            for (int i=0;  i<checkoutItemsList.size();  i++) {

                int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
                int categoryID = checkoutItemsList.get(i).getCustomersBasketProduct().getCategoriesId();


                if (!checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")  ||  !coupon.getExcludeSaleItems().equalsIgnoreCase("1")) {
                    if (!isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories())  ||  coupon.getExcludedProductCategories().size() == 0 ) {
                        if (!isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())  ||  coupon.getExcludeProductIds().size() == 0 ) {
                            if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories())  ||  coupon.getProductCategories().size() == 0 ) {
                                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())  ||  coupon.getProductIds().size() == 0 ) {

                                    discount += (Double.parseDouble(coupon.getAmount()) * checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());
                                }
                            }
                        }
                    }
                }


            }

        }
        else if (coupon.getDiscountType().equalsIgnoreCase("percent_product")) {

            for (int i=0;  i<checkoutItemsList.size();  i++) {

                int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
                int categoryID = checkoutItemsList.get(i).getCustomersBasketProduct().getCategoriesId();


                if (!checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")  ||  !coupon.getExcludeSaleItems().equalsIgnoreCase("1")) {
                    if (!isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories())  ||  coupon.getExcludedProductCategories().size() == 0 ) {
                        if (!isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())  ||  coupon.getExcludeProductIds().size() == 0 ) {
                            if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories())  ||  coupon.getProductCategories().size() == 0 ) {
                                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())  ||  coupon.getProductIds().size() == 0 ) {

                                    double discountOnPrice = (Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsFinalPrice()) * Double.parseDouble(coupon.getAmount())) / 100;
                                    discount += (discountOnPrice * checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());
                                }
                            }
                        }
                    }
                }

            }
        }

        checkoutDiscount += discount;
        coupon.setDiscount(String.valueOf(discount));


        couponsList.add(coupon);
        checkout_coupon_code.setText("");
        couponsAdapter.notifyDataSetChanged();


        setCheckoutTotal();

    }

    private void showSnackBarForCoupon(String msg) {
        final Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private boolean validateCouponProduct(CouponsInfo coupon) {

        int user_used_this_coupon_counter = 0;

        boolean coupon_already_applied = false;

        boolean valid_user_email_for_coupon = false;
        boolean valid_sale_items_in_for_coupon = false;

        boolean any_valid_item_in_cart = false;
        boolean any_valid_category_item_in_cart = false;

        boolean any_non_excluded_item_in_cart = false;
        boolean any_non_excluded_category_item_in_cart = false;


        if (couponsList.size() != 0) {
            for (int i=0;  i<couponsList.size();  i++) {
                if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                    coupon_already_applied = true;
                }
            }
        }


        for (int i=0;  i<coupon.getUsedBy().size();  i++) {
            if (userInfo.getCustomersId().equalsIgnoreCase(coupon.getUsedBy().get(i))) {
                user_used_this_coupon_counter += 1;
            }
        }


        if (coupon.getEmailRestrictions().size() != 0) {
            if (isStringExistsInList(userInfo.getCustomersEmailAddress(), coupon.getEmailRestrictions())) {
                valid_user_email_for_coupon = true;
            }
        }
        else {
            valid_user_email_for_coupon = true;
        }



        for (int i=0;  i<checkoutItemsList.size();  i++) {

            int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
            int categoryID = checkoutItemsList.get(i).getCustomersBasketProduct().getCategoriesId();


            if (!coupon.getExcludeSaleItems().equalsIgnoreCase("1") || !checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")) {
                valid_sale_items_in_for_coupon = true;
            }


            if (coupon.getExcludedProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories())) {
                    any_non_excluded_category_item_in_cart = true;
                }
            } else {
                any_non_excluded_category_item_in_cart = true;
            }

            if (coupon.getExcludeProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())) {
                    any_non_excluded_item_in_cart = true;
                }
            } else {
                any_non_excluded_item_in_cart = true;
            }

            if (coupon.getProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories())) {
                    any_valid_category_item_in_cart = true;
                }
            } else {
                any_valid_category_item_in_cart = true;
            }


            if (coupon.getProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())) {
                    any_valid_item_in_cart = true;
                }
            } else {
                any_valid_item_in_cart = true;
            }

        }


        /////////////////////////////////////////////////////

        if (!disableOtherCoupons) {
            if (!coupon_already_applied) {
                if (!Utilities.checkIsDatePassed(coupon.getExpiryDate())) {
                    if (Integer.parseInt(coupon.getUsageCount()) <= Integer.parseInt(coupon.getUsageLimit())) {
                        if (user_used_this_coupon_counter <= Integer.parseInt(coupon.getUsageLimitPerUser())) {
                            if (valid_user_email_for_coupon) {
                                if (Double.parseDouble(coupon.getMinimumAmount()) <= checkoutTotal) {
                                    if (Double.parseDouble(coupon.getMaximumAmount()) == 0.0  ||  checkoutTotal <= Double.parseDouble(coupon.getMaximumAmount())) {
                                        if (Integer.parseInt(coupon.getLimitUsageToXItems()) == 0  ||  checkoutItemsList.size() <= Integer.parseInt(coupon.getLimitUsageToXItems())) {
                                            if (valid_sale_items_in_for_coupon) {
                                                if (any_non_excluded_category_item_in_cart) {
                                                    if (any_non_excluded_item_in_cart) {
                                                        if (any_valid_category_item_in_cart) {
                                                            if (any_valid_item_in_cart) {

                                                                return true;

                                                            } else {
                                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_products));
                                                                return false;
                                                            }
                                                        } else {
                                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_categories));
                                                            return false;
                                                        }
                                                    } else {
                                                        showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_products));
                                                        return false;
                                                    }
                                                } else {
                                                    showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_categories));
                                                    return false;
                                                }
                                            } else {
                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_sale_items));
                                                return false;
                                            }
                                        } else {
                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_too_many_products));
                                            return false;
                                        }
                                    } else {
                                        showSnackBarForCoupon(getString(R.string.coupon_max_amount_is_less_than_order_total));
                                        return false;
                                    }
                                } else {
                                    showSnackBarForCoupon(getString(R.string.coupon_min_amount_is_greater_than_order_total));
                                    return false;
                                }
                            } else {
                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_you));
                                return false;
                            }
                        } else {
                            showSnackBarForCoupon(getString(R.string.coupon_used_by_you));
                            return false;
                        }
                    } else {
                        showSnackBarForCoupon(getString(R.string.coupon_used_by_all));
                        return false;
                    }
                } else {
                    checkout_coupon_code.setError(getString(R.string.coupon_expired));
                    return false;
                }
            } else {
                showSnackBarForCoupon(getString(R.string.coupon_applied));
                return false;
            }
        } else {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_used_with_existing));
            return false;
        }

    }*/

    private List<CouponsInfo> demoCouponsList() {
        List<CouponsInfo> couponsList = new ArrayList<>();

        CouponsInfo coupon1 = new CouponsInfo();
        coupon1.setCode("PercentProduct_10");
        coupon1.setAmount("10");
        coupon1.setDiscountType("Percent Product");
        coupon1.setDescription("For All Products");

        CouponsInfo coupon2 = new CouponsInfo();
        coupon2.setCode("FixedProduct_10");
        coupon2.setAmount("10");
        coupon2.setDiscountType("Fixed Product");
        coupon2.setDescription("For All Products");

        CouponsInfo coupon3 = new CouponsInfo();
        coupon3.setCode("PercentCart_10");
        coupon3.setAmount("10");
        coupon3.setDiscountType("Percent Cart");
        coupon3.setDescription("For All Products");

        CouponsInfo coupon4 = new CouponsInfo();
        coupon4.setCode("FixedCart_10");
        coupon4.setAmount("10");
        coupon4.setDiscountType("Fixed Cart");
        coupon4.setDescription("For All Products");

        CouponsInfo coupon5 = new CouponsInfo();
        coupon5.setCode("SingleCoupon_50");
        coupon5.setAmount("50");
        coupon5.setDiscountType("Fixed Cart");
        coupon5.setDescription("Individual Use");

        CouponsInfo coupon6 = new CouponsInfo();
        coupon6.setCode("FreeShipping_20");
        coupon6.setAmount("20");
        coupon6.setDiscountType("Fixed Cart");
        coupon6.setDescription("Free Shipping");

        CouponsInfo coupon7 = new CouponsInfo();
        coupon7.setCode("ExcludeSale_15");
        coupon7.setAmount("15");
        coupon7.setDiscountType("Fixed Cart");
        coupon7.setDescription("Not for Sale Items");

        CouponsInfo coupon8 = new CouponsInfo();
        coupon8.setCode("Exclude_Shoes_25");
        coupon8.setAmount("25");
        coupon8.setDiscountType("Fixed Cart");
        coupon8.setDescription("Not For Men Shoes");

        CouponsInfo coupon9 = new CouponsInfo();
        coupon9.setCode("Polo_Shirts_10");
        coupon9.setAmount("10");
        coupon9.setDiscountType("Percent Product");
        coupon9.setDescription("For Men Polo Shirts");

        CouponsInfo coupon10 = new CouponsInfo();
        coupon10.setCode("Jeans_10");
        coupon10.setAmount("10");
        coupon10.setDiscountType("Percent Cart");
        coupon10.setDescription("For Men Jeans");


        couponsList.add(coupon1);
        couponsList.add(coupon2);
        couponsList.add(coupon3);
        couponsList.add(coupon4);
        couponsList.add(coupon5);
        couponsList.add(coupon6);
        couponsList.add(coupon7);
        couponsList.add(coupon8);
        couponsList.add(coupon9);
        couponsList.add(coupon10);

        return couponsList;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public void onDescountProduct(int item_id) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.deleteCartItem(item_id);
        List<CartProduct> cartItemsList = user_cart_db.getCartItems();
        mListener.onFragmentInteraction(String.valueOf(cartItemsList.size()));
        if (cartItemsList.size() == 0) {
            changedFragment();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String value);
    }

    private void RequestShippingRates() {
        String customers_id = mContext.getSharedPreferences("UserInfo", MODE_PRIVATE).getString("userID", "");
        userInfo = userInfoDB.getUserData(customers_id);
        PostTaxAndShippingData postTaxAndShippingData = new PostTaxAndShippingData();

        double productWeight = 0;
        String productWeightUnit = "g";
        List<ProductDetails> productsList = new ArrayList<>();


        // Get ProductWeight, WeightUnit and ProductsList
        for (int i = 0; i < cartItemsList.size(); i++) {
            productWeight += Double.parseDouble(cartItemsList.get(i).getCustomersBasketProduct().getProductsWeight());
            productWeightUnit = cartItemsList.get(i).getCustomersBasketProduct().getProductsWeightUnit();
            productsList.add(cartItemsList.get(i).getCustomersBasketProduct());
        }

        postTaxAndShippingData.setTitle(userInfo.getCustomersFirstname());
        postTaxAndShippingData.setStreetAddress("");
        postTaxAndShippingData.setCity("");
        postTaxAndShippingData.setState("");
        postTaxAndShippingData.setZone("");
        postTaxAndShippingData.setTaxZoneId(0);
        postTaxAndShippingData.setPostcode("");
        postTaxAndShippingData.setCountry("");
        postTaxAndShippingData.setCountryID(0);
        postTaxAndShippingData.setProductsWeight(String.valueOf(productWeight));
        postTaxAndShippingData.setProductsWeightUnit(productWeightUnit);
        postTaxAndShippingData.setProducts(productsList);


        // Proceed API Call to get Tax and Shipping Rates
        Call<ShippingRateData> call = mCallToServer.getShippingMethodsAndTax(postTaxAndShippingData);
        call.enqueue(new Callback<ShippingRateData>() {
            @Override
            public void onResponse(@NonNull Call<ShippingRateData> call, @NonNull retrofit2.Response<ShippingRateData> response) {
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        //addShippingMethods(response.body().getData());
                        addShippingMethods(response.body().getData());
                        CartItemsAdapter cartItemsAdapter = new CartItemsAdapter(cartItemsList, mContext, MyCartFragment.this, Float.parseFloat(shippingService.getRate()));
                        cart_items_recycler.setAdapter(cartItemsAdapter);
                        cart_items_recycler.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                        cart_items_recycler.setLayoutManager(new LinearLayoutManager(mContext));
                        // Show the Cart's Total Price with the help of static method of CartItemsAdapter
                        cartItemsAdapter.setCartTotal();
                        cartItemsAdapter.notifyDataSetChanged();
                        txtEnvio.setText(BeGreenSupport.getInstance().moneyFormat(Float.parseFloat(shippingService.getRate())));


                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ShippingRateData> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addShippingMethods(ShippingRateDetails shippingRateDetails) {
        if (shippingRateDetails.getShippingMethods().getFreeShipping() != null) {
            freeShippingServicesList.addAll(shippingRateDetails.getShippingMethods().getFreeShipping().getServices());
        }

        if (shippingRateDetails.getShippingMethods().getLocalPickup() != null) {
            localPickupServicesList.addAll(shippingRateDetails.getShippingMethods().getLocalPickup().getServices());
        }

        if (shippingRateDetails.getShippingMethods().getFlateRate() != null) {
            flatRateServicesList.addAll(shippingRateDetails.getShippingMethods().getFlateRate().getServices());
        }

        if (shippingRateDetails.getShippingMethods().getUpsShipping() != null) {
            if (shippingRateDetails.getShippingMethods().getUpsShipping().getSuccess().equalsIgnoreCase("1")) {
                upsShippingServicesList.addAll(shippingRateDetails.getShippingMethods().getUpsShipping().getServices());
            }
        }

        if (shippingRateDetails.getShippingMethods().getFreeShipping() != null
                && shippingRateDetails.getShippingMethods().getFreeShipping().getServices().size() > 0) {

            shippingService = shippingRateDetails.getShippingMethods().getFreeShipping().getServices().get(0);

        } else if (shippingRateDetails.getShippingMethods().getLocalPickup() != null
                && shippingRateDetails.getShippingMethods().getLocalPickup().getServices().size() > 0) {

            shippingService = shippingRateDetails.getShippingMethods().getLocalPickup().getServices().get(0);

        } else if (shippingRateDetails.getShippingMethods().getFlateRate() != null
                && shippingRateDetails.getShippingMethods().getFlateRate().getServices().size() > 0) {

            shippingService = shippingRateDetails.getShippingMethods().getFlateRate().getServices().get(0);

        } else if (shippingRateDetails.getShippingMethods().getUpsShipping() != null
                && shippingRateDetails.getShippingMethods().getUpsShipping().getSuccess().equalsIgnoreCase("1")
                && shippingRateDetails.getShippingMethods().getUpsShipping().getServices().size() > 0) {

            shippingService = shippingRateDetails.getShippingMethods().getUpsShipping().getServices().get(0);
        }
    }
}
