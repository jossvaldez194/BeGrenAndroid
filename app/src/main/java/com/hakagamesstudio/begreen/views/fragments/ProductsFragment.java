package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hakagamesstudio.begreen.CallBacks.ProductsCallBack;
import com.hakagamesstudio.begreen.CallBacks.ProductsPresenter;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.product_model.ProductData;
import com.hakagamesstudio.begreen.pojos.product_model.ProductDetails;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.views.activities.Menu_NavDrawer_Acititty;
import com.hakagamesstudio.begreen.views.adapters.ProductsAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductsFragment extends Fragment implements ProductsCallBack.view, View.OnClickListener {
    @BindView(R.id.recyclerProducts)
    RecyclerView recyclerProduts;

    private static final String FRAGMENT_BEFORE = "before";
    private String mViewBefore = "";

    private Context mContext;
    private MaterialDialog mProgressDialog;
    private ProductsPresenter mPresenter;
    private List<ProductDetails> categoryProductsList = new ArrayList<>();
    private AutoCompleteTextView search;
    private ProductsAdapter adapter;

    public ProductsFragment() {

    }

    ProductsFragment newInstance(String viewBefore) {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_BEFORE, viewBefore);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) mContext = getContext();
        if (getArguments() != null)
            mViewBefore = getArguments().getString(FRAGMENT_BEFORE);
        mPresenter = new ProductsPresenter(this);
        if (mViewBefore.equals("Catalogue")) {
            mProgressDialog = BeGreenSupport.getInstance().createProgressDialog("Consultando productos...", mContext);
            mProgressDialog.show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_products, container, false);
        ButterKnife.bind(this, rootview);

        Menu_NavDrawer_Acititty.mFrameCatalogueProduct.setVisibility(View.GONE);
        mPresenter.onSendRequestProduct();
        if (getActivity() != null) {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            search = toolbar.findViewById(R.id.searchEditText);
            search.setVisibility(View.VISIBLE);
            search.setText("");
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Menu_NavDrawer_Acititty.mFrameCatalogueProduct.setVisibility(View.GONE);
                if (adapter != null)
                    adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootview;
    }

    private void initDataSource(List<ProductDetails> datasource) {
        adapter = new ProductsAdapter(datasource, mContext, this);
        recyclerProduts.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerProduts.setHasFixedSize(true);
        recyclerProduts.setAdapter(adapter);
    }

    @Override
    public void onSuccessRequest(ProductData datasource) {
        if (mViewBefore.equals("Catalogue"))
            mProgressDialog.dismiss();
        categoryProductsList.clear();
        categoryProductsList.addAll(datasource.getProductData());
        initDataSource(categoryProductsList);
    }

    @Override
    public void onEmptyProducts(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        ProductDetails data = (ProductDetails) view.getTag();
        if (getActivity() != null) {
            Fragment selected = new SingleProductFragment().newInstance(data);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.contentLayout, selected);
            transaction.commit();
        }
    }
}
