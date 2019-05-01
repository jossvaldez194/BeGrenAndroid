package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.databases.BD_OrcharsFavorites;
import com.hakagamesstudio.begreen.pojos.OrchardFavorites;
import com.hakagamesstudio.begreen.pojos.news_model.all_news.NewsDetails;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.support.ConstantValues;
import com.hakagamesstudio.begreen.support.MyAppPrefsManager;
import com.hakagamesstudio.begreen.views.activities.LoginActivity;
import com.hakagamesstudio.begreen.views.activities.Menu_NavDrawer_Acititty;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class OrchardsDetailsFragment extends Fragment {

    @BindView(R.id.txtDescriptionOrchard_detail)
    WebView txtDescription;
    @BindView(R.id.imgImageOrchard_detail)
    ImageView imgImage;
    @BindView(R.id.txtNameOrchardTitle)
    TextView txtName;

    private Context mContext;
    private static final String ARG_PARAM1 = "Detail";
    private NewsDetails mDetails;
    private AutoCompleteTextView search;
    private BD_OrcharsFavorites BD_Ofavarites = new BD_OrcharsFavorites();
    private Fragment fragment = null;
    private Unbinder mUnbinder;
    private boolean isFavorite = false;
    private String customerID;
    private MyAppPrefsManager myAppPrefsManager;

    public OrchardsDetailsFragment() {
    }

    public OrchardsDetailsFragment newInstance(NewsDetails newsDetails) {
        OrchardsDetailsFragment fragment = new OrchardsDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, newsDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ConstraintLayout mContentOrchardsOptions = BeGreenSupport.getInstance().getmContentrButtonsOrchards();
        mContentOrchardsOptions.setVisibility(View.VISIBLE);
        mContentOrchardsOptions.bringToFront();
        if (getContext() != null) mContext = getContext();
        if (getArguments() != null) {
            mDetails = (NewsDetails) getArguments().getSerializable(ARG_PARAM1);
            BeGreenSupport.getInstance().setNameOrchards(mDetails.getNewsName());
            BeGreenSupport.getInstance().setNewsDetails(mDetails);
        }

        myAppPrefsManager = new MyAppPrefsManager(mContext);
        customerID = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userID", "");
        isFavorite = (customerID == null || !customerID.isEmpty()) && BD_Ofavarites.getOrcharItemFavorite(mDetails.getNewsId()).equals("true");


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deatil_orchard, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        if (getActivity() != null) {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            search = toolbar.findViewById(R.id.searchEditText);
            search.setVisibility(View.GONE);
        }

        if (mDetails != null) {
            txtName.setText(mDetails.getNewsName());
            String description = mDetails.getNewsDescription();
            String styleSheet = "<style> " +
                    "body{background:#eeeeee; margin:0; padding:0} " +
                    "p{color:#666666;} " +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            txtDescription.setHorizontalScrollBarEnabled(false);
            txtDescription.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.mipmap.logo_begreen)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(mContext)
                    .load(ConstantValues.ECOMMERCE_URL + mDetails.getNewsImage())
                    .apply(options)
                    .into(imgImage);

            Menu_NavDrawer_Acititty.imgHome.setOnClickListener(v -> {
                Menu_NavDrawer_Acititty.imgProducts.setTextColor(getResources().getColor(R.color.colorBlack));
                Menu_NavDrawer_Acititty.imgMap.setTextColor(getResources().getColor(R.color.colorBlack));
                Menu_NavDrawer_Acititty.imgProducts.setBackgroundResource(R.mipmap.huertas_boton_prod);
                Menu_NavDrawer_Acititty.imgMap.setBackgroundResource(R.mipmap.huertas_boton_prod);
                fragment = new OrchardsDetailsFragment().newInstance(BeGreenSupport.getInstance().getNewsDetails());
                changedFragment(fragment);
            });

            Menu_NavDrawer_Acititty.imgMap.setOnClickListener(v -> {
                Menu_NavDrawer_Acititty.imgMap.setTextColor(getResources().getColor(R.color.colorWhite));
                Menu_NavDrawer_Acititty.imgProducts.setTextColor(getResources().getColor(R.color.colorBlack));
                Menu_NavDrawer_Acititty.imgMap.setBackgroundResource(R.mipmap.huertas_select);
                Menu_NavDrawer_Acititty.imgProducts.setBackgroundResource(R.mipmap.huertas_boton_prod);
                fragment = new MapOrchardsFragment().newInstance();
                changedFragment(fragment);
            });

            Menu_NavDrawer_Acititty.imgProducts.setOnClickListener(v -> {
                Menu_NavDrawer_Acititty.imgMap.setTextColor(getResources().getColor(R.color.colorWhite));
                Menu_NavDrawer_Acititty.imgMap.setTextColor(getResources().getColor(R.color.colorBlack));
                Menu_NavDrawer_Acititty.imgProducts.setBackgroundResource(R.mipmap.huertas_select);
                Menu_NavDrawer_Acititty.imgMap.setBackgroundResource(R.mipmap.huertas_boton_prod);
                String nameOrchards = BeGreenSupport.getInstance().getNameOrchards();
                fragment = new OrchardsProductsFragment().newInstance(nameOrchards);
                changedFragment(fragment);
            });

        }

        imgImage.setOnClickListener(v -> {
            String web_url = BeGreenSupport.getInstance().getAppSettingsDetails().getSiteUrl();
            if (!web_url.startsWith("https://")  &&  !web_url.startsWith("http://"))
                web_url = "http://" + web_url;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web_url)));
        });
        return rootView;
    }

    private void changedFragment(Fragment selected) {
        if (getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.contentLayout, selected);
            transaction.commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorits, menu);
        MenuItem bedMenuItem = menu.findItem(R.id.menu_favorite);
        if (isFavorite) {
            bedMenuItem.setIcon(R.drawable.ic_favorite_black_24dp);
        } else {
            bedMenuItem.setIcon(R.drawable.ic_favorite_border_black_24dp);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                if (myAppPrefsManager.isUserLoggedIn()) {
                    if (isFavorite) {
                        isFavorite = false;
                        item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                        deleteToFavorites(Integer.parseInt(mDetails.getNewsId()));
                    } else {
                        isFavorite = true;
                        item.setIcon(R.drawable.ic_favorite_black_24dp);
                        addToFavorites();
                    }
                }else {
                    isFavorite = false;
                    Intent i = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(i);
                    if (getActivity() != null) {
                        getActivity().overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

    }

    private void addToFavorites() {
        OrchardFavorites favorites = new OrchardFavorites();
        favorites.setOrchards_id(mDetails.getNewsId());
        favorites.setOrchards_userId(customerID);
        favorites.setOrchards_name(mDetails.getNewsName());
        favorites.setOrchards_favorite("true");
        favorites.setOrchards_image(ConstantValues.ECOMMERCE_URL + mDetails.getNewsImage());
        BD_Ofavarites.insertUserData(favorites);
        Toast.makeText(mContext, "Huerta agregada de tus favoritos", Toast.LENGTH_SHORT).show();

        ArrayList<OrchardFavorites> list = BD_Ofavarites.getCartItems(customerID);
        Log.i("favoritos", String.valueOf(list.size()));
    }

    private void deleteToFavorites(int idOrchard) {
        BD_Ofavarites.deleteCartItem(idOrchard);
        Toast.makeText(mContext, "Huerta eliminada de tus favoritos", Toast.LENGTH_SHORT).show();
        ArrayList<OrchardFavorites> list = BD_Ofavarites.getCartItems(customerID);
        Log.i("favoritos", String.valueOf(list.size()));
    }
}
