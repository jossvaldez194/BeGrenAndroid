package com.hakagamesstudio.begreen.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hakagamesstudio.begreen.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MapOrchardsFragment extends Fragment {

    @BindView(R.id.imgMapOrchards)
    ImageView mMaps;

    private Unbinder mUnbinder;

    public MapOrchardsFragment() {
    }

    public MapOrchardsFragment newInstance() {
        MapOrchardsFragment fragment = new MapOrchardsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_map_orchards, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        mMaps.setImageResource(R.mipmap.map_default);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
