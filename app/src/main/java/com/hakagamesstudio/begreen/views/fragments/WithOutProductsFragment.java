package com.hakagamesstudio.begreen.views.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hakagamesstudio.begreen.R;

public class WithOutProductsFragment extends Fragment {

    @BindView(R.id.imageView3)
    ImageView imageView;
    @BindView(R.id.txtDescriptionEmptyView)
    TextView txtDescription;

    private static final String ARG_PARAM1 = "imagen";
    private static final String ARG_PARAM2 = "descripcion";

    // TODO: Rename and change types of parameters
    private int imageName;
    private String textDescription;

    private Unbinder mUnbinder;

    public WithOutProductsFragment() {

    }

    public static WithOutProductsFragment newInstance(int param1, String param2) {
        WithOutProductsFragment fragment = new WithOutProductsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageName = getArguments().getInt(ARG_PARAM1);
            textDescription = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_with_out_products, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        imageView.setBackgroundResource(imageName);
        txtDescription.setText(textDescription);

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
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
