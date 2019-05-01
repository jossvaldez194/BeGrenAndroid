package com.hakagamesstudio.begreen.views.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hakagamesstudio.begreen.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class IntroFragment01 extends Fragment {

    @BindView(R.id.imgImageIntro)
    ImageView imgImageIntro;
    @BindView(R.id.txtclicktotect)
    TextView clickText;

    private static final String IMAGE_INTRO = "imagen";
    private int mImageIntro;
    private Unbinder mUnbinder;

    public IntroFragment01() {
    }


    public IntroFragment01 newInstance(int slider) {
        IntroFragment01 fragment = new IntroFragment01();
        Bundle args = new Bundle();
        args.putInt(IMAGE_INTRO, slider);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageIntro = getArguments().getInt(IMAGE_INTRO);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro_01, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        switch (mImageIntro) {
            case 0:
                imgImageIntro.setImageResource(R.mipmap.tuto_01_0);
                break;
            case 1:
                imgImageIntro.setImageResource(R.mipmap.tuto_1);
                clickText.setOnClickListener(v -> {
                    String web_url = "http://be-green.mx";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web_url)));
                });
                break;
            case 2:
                imgImageIntro.setImageResource(R.mipmap.tuto_2);
                break;
            case 3:
                imgImageIntro.setImageResource(R.mipmap.tuto_3);
                break;
            case 4:
                imgImageIntro.setImageResource(R.mipmap.tuto_4);
                break;
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
