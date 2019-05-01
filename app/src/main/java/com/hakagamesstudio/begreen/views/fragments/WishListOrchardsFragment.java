package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.databases.BD_OrcharsFavorites;
import com.hakagamesstudio.begreen.pojos.OrchardFavorites;
import com.hakagamesstudio.begreen.support.MyAppPrefsManager;
import com.hakagamesstudio.begreen.views.adapters.WhishListOrchardsAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class WishListOrchardsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.whishlistOrchards_recycler)
    RecyclerView recyclerViewOrchardsFav;
    private Unbinder mUnbinder;

    private BD_OrcharsFavorites BD_Favorites = new BD_OrcharsFavorites();
    private ArrayList<OrchardFavorites> mListFavorites;
    private Context mContext;
    private WhishListOrchardsAdapter adapter;
    private MyAppPrefsManager myAppPrefsManager;
    private String customerID;

    public WishListOrchardsFragment() {
        // Required empty public constructor
    }

    public WishListOrchardsFragment newInstance() {
        WishListOrchardsFragment fragment = new WishListOrchardsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) mContext = getContext();
        customerID = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userID", "");
        myAppPrefsManager = new MyAppPrefsManager(getContext());
        myAppPrefsManager = new MyAppPrefsManager(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wish_list_orchards, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        if (myAppPrefsManager.isUserLoggedIn()) {
            mListFavorites = BD_Favorites.getCartItems(customerID);
            if (mListFavorites.size() > 0) {
                adapter = new WhishListOrchardsAdapter(mListFavorites, mContext, null);
                recyclerViewOrchardsFav.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                recyclerViewOrchardsFav.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerViewOrchardsFav.setAdapter(adapter);
            } else {
                changedFragment(R.mipmap.app_favoritos, "No tienes productos favoritos :(");
            }
        } else {
            changedFragment(R.mipmap.app_favoritos, "No tienes huertas favoritas :(");
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        OrchardFavorites favorites = (OrchardFavorites) v.getTag();
        int index = 0;
        for (int i = 0; i < mListFavorites.size(); i++) {
            if (favorites.getOrchards_id().equals(mListFavorites.get(i).getOrchards_id())) {
                index = i;
                break;
            }
        }
        BD_Favorites.deleteCartItem(Integer.parseInt(favorites.getOrchards_id()));
        adapter.removeAt(index);
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
}
