package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.RoomDatabase.BeGreenDB;
import com.hakagamesstudio.begreen.RoomDatabase.CreditCard;
import com.hakagamesstudio.begreen.support.ConstantValues;
import com.hakagamesstudio.begreen.views.adapters.CreditCardAdapter;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CreditCardsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.recyclerCardPayment)
    RecyclerView mRecyclerPayment;
    @BindView(R.id.txtAddCard)
    TextView txtAddCard;
    @BindView(R.id.imgAddCart)
    ImageView imgAddCard;
    @BindView(R.id.contentAddNewCard)
    ConstraintLayout contentAddCard;

    private Unbinder mUnbider;
    private Context mContext;

    private String spinItemSeelctedAno = "";
    private String spinItemSeelctedMes = "";
    private CardType cardType;

    private CreditCardAdapter mAdapter;

    private BeGreenDB mBeGreenBD;
    private String customerID;
    private String PAYMENT_CURRENCY = "MXN";
    private String STRIPE_PUBLISHABLE_KEY = "";
    private String PAYPAL_PUBLISHABLE_KEY = "";
    private static PayPalConfiguration payPalConfiguration;
    BraintreeFragment braintreeFragment;


    public CreditCardsFragment() {

    }

    public static CreditCardsFragment newInstance() {
        CreditCardsFragment fragment = new CreditCardsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) mContext = getContext();
        mBeGreenBD = BeGreenDB.getAppDatabase(mContext);
        customerID = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userID", "");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_credit_cards, container, false);
        mUnbider = ButterKnife.bind(this, rootView);

        if (0 < mBeGreenBD.mPickPalDAO().getAll(customerID).size()) {
            initAdapter();
            imgAddCard.setVisibility(View.GONE);
            txtAddCard.setVisibility(View.GONE);
        }

        imgAddCard.setOnClickListener(v -> startBillingAgreement());
        //imgAddCard.setOnClickListener(v -> showCardDialog());
        contentAddCard.setOnClickListener(v -> showCardDialog());
        return rootView;
    }

    public void startBillingAgreement() {
        PayPalRequest request = new PayPalRequest()
                .localeCode("MNX")
                .billingAgreementDescription("Your agreement description");

        PayPal.requestBillingAgreement(braintreeFragment, request);
    }

    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        // Send nonce to server
        String nonce = paymentMethodNonce.getNonce();
        if (paymentMethodNonce instanceof PayPalAccountNonce) {
            PayPalAccountNonce paypalAccountNonce = (PayPalAccountNonce) paymentMethodNonce;

            // Access additional information
            String email = paypalAccountNonce.getEmail();
            String firstName = paypalAccountNonce.getFirstName();
            String lastName = paypalAccountNonce.getLastName();
            String phone = paypalAccountNonce.getPhone();

            // See PostalAddress.java for details
            PostalAddress billingAddress = paypalAccountNonce.getBillingAddress();
            PostalAddress shippingAddress = paypalAccountNonce.getShippingAddress();
        }
    }

    private void showCardDialog() {









        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.custom_add_card, false)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();

        SupportedCardTypesView braintreeSupportedCards;
        final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.MAESTRO,
                CardType.UNIONPAY, CardType.AMEX};

        AppCompatEditText tarjetaHabiente = dialog.getView().findViewById(R.id.editTarjetaHabiente_card);
        AppCompatEditText numeroTarjeta = dialog.getView().findViewById(R.id.editNumeroTarjeta_card);
        //AppCompatEditText numeroSeguridad = dialog.getView().findViewById(R.id.editNumeroSeguridad_card);
        Spinner mesTarjeta = dialog.getView().findViewById(R.id.txtMes_card);
        Spinner anoTarjeta = dialog.getView().findViewById(R.id.txtAno_card);
        Button btnGuardarTarjeta = dialog.getView().findViewById(R.id.btnGuardarTarjeta);
        ImageView imgClose = dialog.getView().findViewById(R.id.imgCloseDialog);
        braintreeSupportedCards = dialog.getView().findViewById(R.id.supported_card_types);
        braintreeSupportedCards.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        anoTarjeta.setOnItemSelectedListener(new ItemAnoSelected());
        mesTarjeta.setOnItemSelectedListener(new ItemMesSelected());

        numeroTarjeta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!numeroTarjeta.getText().toString().trim().isEmpty()) {
                    CardType type = CardType.forCardNumber(numeroTarjeta.getText().toString());
                    if (cardType != type) {
                        cardType = type;

                        InputFilter[] filters = { new InputFilter.LengthFilter(cardType.getMaxCardLength()) };
                        numeroTarjeta.setFilters(filters);
                        numeroTarjeta.invalidate();

                        braintreeSupportedCards.setSelected(cardType);
                    }
                } else {
                    braintreeSupportedCards.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnGuardarTarjeta.setOnClickListener(v -> {
            if (evaluateFieldsCar(tarjetaHabiente.getText().toString(), numeroTarjeta.getText().toString(),
                    /*numeroSeguridad.getText().toString(),*/ spinItemSeelctedMes, spinItemSeelctedAno).isEmpty()) {

                com.hakagamesstudio.begreen.RoomDatabase.CreditCard creditCard = new com.hakagamesstudio.begreen.RoomDatabase.CreditCard();
                creditCard.setIdUser(customerID);
                creditCard.setNameUser(tarjetaHabiente.getText().toString());
                creditCard.setMonth(spinItemSeelctedMes);
                creditCard.setYear(spinItemSeelctedAno);
                creditCard.setNumberTarjet(numeroTarjeta.getText().toString());

                mBeGreenBD.mPickPalDAO().replaceCreditCard(creditCard);

                if (1 <= mBeGreenBD.mPickPalDAO().getAll(customerID).size()) {
                    initAdapter();
                } else {
                    mAdapter.updateListCard(mBeGreenBD.mPickPalDAO().getAll(customerID));
                    mAdapter.notifyDataSetChanged();
                }

                mRecyclerPayment.setVisibility(View.VISIBLE);
                contentAddCard.setVisibility(View.VISIBLE);
                imgAddCard.setVisibility(View.GONE);
                txtAddCard.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        imgClose.setOnClickListener(v -> dialog.dismiss());

    }

    private String evaluateFieldsCar(String tarjetaHabiente, String numeroTarjeta/*, String numeroSeguridad*/, String mesTarjeta, String anoTarjeta) {
        if (!tarjetaHabiente.isEmpty()) {
            String mNameTarjetaHabiente = tarjetaHabiente.trim();
        } else {
            return "Debe ingresar un tarjeta habiente";
        }

        if (!numeroTarjeta.isEmpty()) {
            if (numeroTarjeta.length() == 16) {
                String mNumeroTarjeta = numeroTarjeta;
            } else {
                return "El número de tarjeta no es valido";
            }
        } else {
            return "No se ha ingresado un número de tarjeta";
        }

        if (!mesTarjeta.isEmpty()) {
            String mMesTarjeta = mesTarjeta;
        } else {
            return "Debe indicar el mes de expiración";
        }

        if (!anoTarjeta.isEmpty()) {
            String mAnoTarjeta = anoTarjeta;
        } else {
            return "Debe indicar el año de expiración";
        }

        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbider.unbind();
    }

    public void initAdapter() {
        if (0 < mBeGreenBD.mPickPalDAO().getAll(customerID).size()) {
            mAdapter = new CreditCardAdapter(mBeGreenBD.mPickPalDAO().getAll(customerID), this);
            mRecyclerPayment.setVisibility(View.VISIBLE);
            contentAddCard.setVisibility(View.VISIBLE);
            mRecyclerPayment.setHasFixedSize(true);
            mRecyclerPayment.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerPayment.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL));
            mRecyclerPayment.setAdapter(mAdapter);
            spinItemSeelctedAno = "";
            spinItemSeelctedMes = "";
        }else{
            imgAddCard.setVisibility(View.VISIBLE);
            txtAddCard.setVisibility(View.VISIBLE);
            contentAddCard.setVisibility(View.GONE);
            mRecyclerPayment.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgDeleteCard_item:
                CreditCard creditCard = (CreditCard) v.getTag();

                new MaterialDialog.Builder(mContext)
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .title("BeGreen")
                        .content("¿Desea eliminar la tarjeta seleccionada?")
                        .positiveText("Aceptar")
                        .negativeText("Cancelar")
                        .onPositive((dialog1, which) -> {
                            mBeGreenBD.mPickPalDAO().deletefromnumberTarjet(creditCard.getNumberTarjet());
                            initAdapter();
                        })
                        .onNegative((dialog12, which) -> dialog12.dismiss())
                        .show();
                break;

            case R.id.imgEditCreditCard:

                break;
        }

    }

    public class ItemAnoSelected implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            spinItemSeelctedAno = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }
    }

    public class ItemMesSelected implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            spinItemSeelctedMes = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }

    }
}

