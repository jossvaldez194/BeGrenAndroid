package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.app.App;
import com.hakagamesstudio.begreen.databases.User_Cart_DB;
import com.hakagamesstudio.begreen.databases.User_Info_DB;
import com.hakagamesstudio.begreen.pojos.address_model.AddressDetails;
import com.hakagamesstudio.begreen.pojos.cart_model.CartProduct;
import com.hakagamesstudio.begreen.pojos.cart_model.CartProductAttributes;
import com.hakagamesstudio.begreen.pojos.order_model.OrderData;
import com.hakagamesstudio.begreen.pojos.order_model.PostOrder;
import com.hakagamesstudio.begreen.pojos.order_model.PostProducts;
import com.hakagamesstudio.begreen.pojos.order_model.PostProductsAttributes;
import com.hakagamesstudio.begreen.pojos.product_model.Option;
import com.hakagamesstudio.begreen.pojos.product_model.Value;
import com.hakagamesstudio.begreen.pojos.shipping_model.ShippingService;
import com.hakagamesstudio.begreen.pojos.user_model.UserDetails;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class CheckOutFragment extends Fragment {

    @BindView(R.id.txtAddressDelvery)
    TextView txtAddress;
    @BindView(R.id.txtComentsOrders)
    TextView txtComent;
    @BindView(R.id.txtNameClient)
    TextView txtNameClients;
    @BindView(R.id.txtCreditCardClient)
    TextView txtCreditCardClient;
    @BindView(R.id.txtSubtotalCheckOut)
    TextView txtSubtotalt;
    @BindView(R.id.txtEnvioCheckOut)
    TextView txtEnvio;
    @BindView(R.id.editCuponCheckOut)
    TextView txtCupon;
    @BindView(R.id.txtTotal_checkout_fragment)
    TextView txtTotal;
    @BindView(R.id.btn_payment)
    Button btnPay;
    @BindView(R.id.imgEditNameAddress)
    ImageView imgNameAddress;
    @BindView(R.id.imgEditComentProduct)
    ImageView imgComentProduct;

    private String mSubtotal;
    private String mEnvio;
    private String mCupon;
    private String mTotal;

    private String tax;
    private Unbinder mUnbinder;
    private Context mContext;
    private UserDetails userInfo;
    private AddressDetails shippingAddress;
    private ShippingService shippingMethod;
    private AddressDetails billingAddress;
    private CallBackRetrofit callBackRetrofit = ApiUtils.getAPIService();
    private User_Info_DB user_info_db = new User_Info_DB();
    private User_Cart_DB user_cart_db = new User_Cart_DB();
    private ArrayList<CartProduct> checkoutItemsList = new ArrayList<>();

    private double checkoutTax, checkoutShipping = 0.0;

    public CheckOutFragment() {
    }

    public static CheckOutFragment newInstance() {
        CheckOutFragment fragment = new CheckOutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) mContext = getContext();
        checkoutItemsList = user_cart_db.getCartItems();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_out, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        tax = ((App) getContext().getApplicationContext()).getTax();
        shippingMethod = ((App) getContext().getApplicationContext()).getShippingService();
        checkoutTax = Double.parseDouble("0.0");
        shippingAddress = ((App) getContext().getApplicationContext()).getShippingAddress();
        //checkoutShipping = checkoutShippingCost = Double.parseDouble(shippingMethod.getRate());
        billingAddress = ((App) getContext().getApplicationContext()).getBillingAddress();
        userInfo = user_info_db.getUserData(getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE).getString("userID", null));
        txtAddress.setText(mContext.getSharedPreferences("UserInfo", MODE_PRIVATE).getString("DireccionUser", ""));
        txtNameClients.setText(mContext.getSharedPreferences("UserInfo", MODE_PRIVATE).getString("userName", ""));
        if (getArguments() != null) {
            mSubtotal = getArguments().getString("subTotal", "");
            txtSubtotalt.setText(mSubtotal);
            mEnvio = getArguments().getString("envio", "");
            txtEnvio.setText(mEnvio);
            mCupon = getArguments().getString("cupon", "");
            txtCupon.setText(mCupon);
            mTotal = getArguments().getString("total", "");
            txtTotal.setText(mTotal);
        }

        btnPay.setOnClickListener(v -> proceedOrder());

        imgNameAddress.setOnClickListener(v -> showDialogDescription(true));
        imgComentProduct.setOnClickListener(v -> showDialogDescription(false));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void showDialogDescription(boolean fromAddress) {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.custom_content_change_name, false)
                .canceledOnTouchOutside(false)
                .progressIndeterminateStyle(false)
                .show();

        Button btnSaveDes = dialog.getView().findViewById(R.id.btn_save_new_name);
        EditText editDes = dialog.getView().findViewById(R.id.editNameChangeProfile);
        EditText editOldPass = dialog.getView().findViewById(R.id.editOldPassword);
        ImageView imgClose = dialog.getView().findViewById(R.id.btnCloseDialogName);
        TextView txtTitle = dialog.getView().findViewById(R.id.textView13);

        editOldPass.setVisibility(View.GONE);
        if (!fromAddress) {
            txtTitle.setText("Ingresar comentario");
            editDes.setHint("Ej. Entregar despues de las 3 de la tarde, casa con porton blanco");
            btnSaveDes.setText("Guardar comentario");
        } else {
            txtTitle.setText("Ingresar dirección");
            editDes.setHint("Nueva dirección");
            btnSaveDes.setText("Guardar dirección");
        }

        imgClose.setOnClickListener(v -> dialog.dismiss());
        btnSaveDes.setOnClickListener(v -> {
            if (!editDes.getText().toString().isEmpty()) {
                dialog.dismiss();
                if (!fromAddress) {
                    txtComent.setText(editDes.getText().toString());
                } else {
                    txtAddress.setText(editDes.getText().toString());
                }
            } else {
                Toast.makeText(mContext, "No ha ingresado ningun comentario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedOrder() {
        PostOrder orderDetails = new PostOrder();
        List<PostProducts> orderProductList = new ArrayList<>();

        for (int i = 0; i < checkoutItemsList.size(); i++) {

            PostProducts orderProduct = new PostProducts();

            // Get current Product Details
            orderProduct.setProductsId(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId());
            orderProduct.setProductsName(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsName());
            orderProduct.setModel(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsModel());
            orderProduct.setImage(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsImage());
            orderProduct.setWeight(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsWeight());
            orderProduct.setUnit(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsWeightUnit());
            orderProduct.setManufacture(checkoutItemsList.get(i).getCustomersBasketProduct().getManufacturersName());
            orderProduct.setCategoriesId(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoriesId());
            orderProduct.setCategoriesName(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoriesName());
            orderProduct.setPrice(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsPrice());
            orderProduct.setFinalPrice(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsFinalPrice());
            orderProduct.setSubtotal(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
            orderProduct.setTotal(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
            orderProduct.setCustomersBasketQuantity(checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());

            orderProduct.setOnSale(checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1"));


            List<PostProductsAttributes> productAttributes = new ArrayList<>();

            for (int j = 0; j < checkoutItemsList.get(i).getCustomersBasketProductAttributes().size(); j++) {
                CartProductAttributes cartProductAttributes = checkoutItemsList.get(i).getCustomersBasketProductAttributes().get(j);
                Option attributeOption = cartProductAttributes.getOption();
                Value attributeValue = cartProductAttributes.getValues().get(0);

                PostProductsAttributes attribute = new PostProductsAttributes();
                attribute.setProductsOptionsId(String.valueOf(attributeOption.getId()));
                attribute.setProductsOptions(attributeOption.getName());
                attribute.setProductsOptionsValuesId(String.valueOf(attributeValue.getId()));
                attribute.setProductsOptionsValues(attributeValue.getValue());
                attribute.setOptionsValuesPrice(attributeValue.getPrice());
                attribute.setPricePrefix(attributeValue.getPricePrefix());
                attribute.setAttributeName(attributeValue.getValue() + " " + attributeValue.getPricePrefix() + attributeValue.getPrice());

                productAttributes.add(attribute);
            }

            orderProduct.setAttributes(productAttributes);


            // Add current Product to orderProductList
            orderProductList.add(orderProduct);
        }


        // Set Customer Info
        orderDetails.setCustomersId(Integer.parseInt(userInfo.getCustomersId()));
        orderDetails.setCustomersName(userInfo.getCustomersFirstname());
        orderDetails.setCustomersTelephone(userInfo.getCustomersTelephone());
        orderDetails.setCustomersEmailAddress(userInfo.getCustomersEmailAddress());

        // Set Shipping  Info
        orderDetails.setDeliveryFirstname(shippingAddress.getFirstname());
        orderDetails.setDeliveryLastname(shippingAddress.getLastname());
        orderDetails.setDeliveryStreetAddress(shippingAddress.getStreet());
        orderDetails.setDeliveryPostcode(shippingAddress.getPostcode());
        orderDetails.setDeliverySuburb(shippingAddress.getSuburb());
        orderDetails.setDeliveryCity(shippingAddress.getCity());
        orderDetails.setDeliveryZone(shippingAddress.getZoneName());
        orderDetails.setDeliveryState(shippingAddress.getZoneName());
        orderDetails.setDeliverySuburb(shippingAddress.getZoneName());
        orderDetails.setDeliveryCountry(shippingAddress.getCountryName());
        orderDetails.setDeliveryZoneId(String.valueOf(shippingAddress.getZoneId()));
        orderDetails.setDeliveryCountryId(String.valueOf(shippingAddress.getCountriesId()));

        // Set Billing Info
        orderDetails.setBillingFirstname(billingAddress.getFirstname());
        orderDetails.setBillingLastname(billingAddress.getLastname());
        orderDetails.setBillingStreetAddress(billingAddress.getStreet());
        orderDetails.setBillingPostcode(billingAddress.getPostcode());
        orderDetails.setBillingSuburb(billingAddress.getSuburb());
        orderDetails.setBillingCity(billingAddress.getCity());
        orderDetails.setBillingZone(billingAddress.getZoneName());
        orderDetails.setBillingState(billingAddress.getZoneName());
        orderDetails.setBillingSuburb(billingAddress.getZoneName());
        orderDetails.setBillingCountry(billingAddress.getCountryName());
        orderDetails.setBillingZoneId(String.valueOf(billingAddress.getZoneId()));
        orderDetails.setBillingCountryId(String.valueOf(billingAddress.getCountriesId()));

        orderDetails.setTaxZoneId(shippingAddress.getZoneId());
        orderDetails.setTotalTax(checkoutTax);
        orderDetails.setShippingCost(checkoutShipping);
//      orderDetails.setShippingMethod(shippingMethod.getName() + " (" + shippingMethod.getShippingMethod() + ")");
        orderDetails.setShippingMethod("");

        orderDetails.setComments(txtComent.getText().toString().trim());

        /*if (couponsList.size() > 0) {
            orderDetails.setIsCouponApplied(1);
        } else {
            orderDetails.setIsCouponApplied(0);
        }*/
        orderDetails.setIsCouponApplied(0);
        orderDetails.setCouponAmount(0.0);
        orderDetails.setCoupons(null);

        // Set PaymentNonceToken and PaymentMethod
        orderDetails.setNonce("kjbcsfsgohiuickdsvhcvs");
        orderDetails.setPaymentMethod("cash_on_delivery");

        // Set Checkout Price and Products
        orderDetails.setProductsTotal(Double.parseDouble("401"));
        orderDetails.setTotalPrice(Double.parseDouble("401"));
        orderDetails.setProducts(orderProductList);

        PlaceOrderNow(orderDetails);

    }

    private void PlaceOrderNow(PostOrder postOrder) {
        Call<OrderData> call = callBackRetrofit.addToOrder(postOrder);
        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(@NonNull Call<OrderData> call, @NonNull retrofit2.Response<OrderData> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Clear User's Cart
                        User_Cart_DB user_cart_db = new User_Cart_DB();
                        user_cart_db.clearCart();

                        // Clear User's Shipping and Billing info from AppContext
                        ((App) getContext().getApplicationContext()).setShippingAddress(new AddressDetails());
                        ((App) getContext().getApplicationContext()).setBillingAddress(new AddressDetails());

                        Toast.makeText(mContext, "Tu compra se ha realizado con exito", Toast.LENGTH_SHORT).show();
                        // Navigate to Thank_You Fragment
                        /*Fragment fragment = new Thank_You();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.popBackStack(getString(R.string.actionCart), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.contentLayout, fragment)
                                .commit();*/
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
            public void onFailure(@NonNull Call<OrderData> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }
}
