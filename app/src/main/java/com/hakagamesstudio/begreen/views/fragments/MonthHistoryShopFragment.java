package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.order_model.OrderData;
import com.hakagamesstudio.begreen.pojos.order_model.OrderDetails;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.support.ConstantValues;
import com.hakagamesstudio.begreen.views.adapters.ItemsHistoryAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class MonthHistoryShopFragment extends Fragment {

    @BindView(R.id.rvListShop)
    RecyclerView rvListShop;
    @BindView(R.id.contentListShop)
    LinearLayout contentListShop;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MaterialDialog mProgressDialog;

    private String mParam1;
    private String mDate;
    private Context mContext;
    private List<OrderDetails> ordersList = new ArrayList<>();
    private CallBackRetrofit mCallServer = ApiUtils.getAPIService();

    private Unbinder mUnbinder;
    private OnFragmentInteractionListener mListener;

    public MonthHistoryShopFragment() {

    }

    public static MonthHistoryShopFragment newInstance(String param1, String date) {
        MonthHistoryShopFragment fragment = new MonthHistoryShopFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) mContext = getContext();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mDate = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month_history_shop, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        ordersList.size();
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        Date date;
        try {
            date = format.parse(mDate);
            Log.i("Fecha", String.valueOf(date));
            System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
        } catch (ParseException e) {
            e.printStackTrace();
        }
        RequestMyOrders();
        return view;
    }

/*
    @Override
    public void onAttach(Context context) {
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
*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void RequestMyOrders() {
        mProgressDialog = BeGreenSupport.getInstance().createProgressDialog("Consultando tus compras", mContext);
        mProgressDialog.show();
        String customerID = mContext.getSharedPreferences("UserInfo", MODE_PRIVATE).getString("userID", "");
        Call<OrderData> call = mCallServer.getOrders(customerID, ConstantValues.LANGUAGE_ID);
        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(@NonNull Call<OrderData> call, @NonNull retrofit2.Response<OrderData> response) {
                mProgressDialog.dismiss();
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            addOrders(response.body());
                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            contentListShop.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            contentListShop.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderData> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addOrders(OrderData orderData) {
        ordersList = orderData.getData();
        if (ordersList.size() > 0) {
            ItemsHistoryAdapter adapter = new ItemsHistoryAdapter();
            rvListShop.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            rvListShop.setHasFixedSize(true);
            rvListShop.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            contentListShop.setVisibility(View.VISIBLE);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
