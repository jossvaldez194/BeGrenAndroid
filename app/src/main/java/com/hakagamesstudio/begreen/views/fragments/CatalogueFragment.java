package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.category_model.CategoryDetails;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.views.activities.Menu_NavDrawer_Acititty;
import com.hakagamesstudio.begreen.views.adapters.CatalogueProductFilterAdapterAux;
import com.hakagamesstudio.begreen.views.adapters.MenuCategoryAdapter;

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
import butterknife.Unbinder;

public class CatalogueFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.recyclerMenu)
    RecyclerView recyclerMenu;

    private List<CategoryDetails> allCategoriesList = new ArrayList<>();
    private List<CategoryDetails> mFilterListCategory = new ArrayList<>();
    private List<CategoryDetails> subCategoriesList = new ArrayList<>();
    private List<CategoryDetails> mListProductSearch = new ArrayList<>();

    private Unbinder mUnbinder;
    private Context mContext;

    //private AutoCompleteTextView search;

    public CatalogueFragment() {

    }

    public CatalogueFragment newInstance() {
        CatalogueFragment fragment = new CatalogueFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) mContext = getContext();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalogue, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

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

        allCategoriesList.clear();
        allCategoriesList = BeGreenSupport.getInstance().getCategoriesList();
        setDataSource();

        MenuCategoryAdapter adapter = new MenuCategoryAdapter(mFilterListCategory, this, mContext);
        recyclerMenu.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerMenu.setHasFixedSize(true);
        recyclerMenu.setAdapter(adapter);

        CatalogueProductFilterAdapterAux adapterAux = new CatalogueProductFilterAdapterAux(mListProductSearch, mContext, this);
        Menu_NavDrawer_Acititty.mRecyclerCatalogueProduct.setLayoutManager(new LinearLayoutManager(mContext));
        Menu_NavDrawer_Acititty.mRecyclerCatalogueProduct.setHasFixedSize(true);
        Menu_NavDrawer_Acititty.mRecyclerCatalogueProduct.setAdapter(adapterAux);
        Menu_NavDrawer_Acititty.mFrameCatalogueProduct.setVisibility(View.GONE);

        /*search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    Menu_NavDrawer_Acititty.mFrameCatalogueProduct.setVisibility(View.GONE);
                }else{
                    Menu_NavDrawer_Acititty.mFrameCatalogueProduct.setVisibility(View.VISIBLE);
                    adapterAux.getFilter().filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        //Filtra para buscar las subcategorias
        subCategoriesList.clear();
        if (view.getId() == R.id.layoutProductAux){
            CategoryDetails categoryDetails = (CategoryDetails)view.getTag();
            subCategoriesList.add(categoryDetails);
        }else {
            for (int i = 0; i < allCategoriesList.size(); i++) {
                if (allCategoriesList.get(i).getParentId().equalsIgnoreCase(String.valueOf(Integer.parseInt(mFilterListCategory.get(recyclerMenu.getChildAdapterPosition(view)).getId())))) {
                    subCategoriesList.add(allCategoriesList.get(i));
                }
            }
        }
        BeGreenSupport.getInstance().setSubCategoryList(subCategoriesList);
        changedFragment();
    }

    private void setDataSource() {
        for (int i = 0; i < allCategoriesList.size(); i++) {
            if (allCategoriesList.get(i).getParentId().equalsIgnoreCase("0")) {
                mFilterListCategory.add(allCategoriesList.get(i));
            }else{
                mListProductSearch.add(allCategoriesList.get(i));
            }
        }
    }

    private void changedFragment() {
        if (getActivity() != null) {
            Fragment selected = new ProductsFragment().newInstance("Catalogue");
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.contentLayout, selected);
            transaction.commit();
        }
    }
}
