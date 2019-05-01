package com.hakagamesstudio.begreen.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.infoOfferts;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.views.adapters.OffertsAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OffersFragment extends Fragment {

    @BindView(R.id.recyclerOfferts)
    RecyclerView recyclerOfferts;

    private Unbinder mUnbinder;
    private CallBackRetrofit mCall = ApiUtils.getAPIService();

    public OffersFragment() {
    }

    public  OffersFragment newInstance() {
        OffersFragment fragment = new OffersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_offers, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        if (getActivity() != null) {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            AutoCompleteTextView search = toolbar.findViewById(R.id.searchEditText);
            search.setVisibility(View.GONE);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null){
                actionBar.setTitle("");
            }

        }

        Call<List<infoOfferts>> getOfferts = mCall.getAllOferts();
        getOfferts.enqueue(new Callback<List<infoOfferts>>() {
            @Override
            public void onResponse(Call<List<infoOfferts>> call, Response<List<infoOfferts>> response) {
                if (response.body() != null){
                    if (response.code() == 200){
                        OffertsAdapter adapter = new OffertsAdapter(response.body(), getContext());
                        recyclerOfferts.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerOfferts.setHasFixedSize(true);
                        recyclerOfferts.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<infoOfferts>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
