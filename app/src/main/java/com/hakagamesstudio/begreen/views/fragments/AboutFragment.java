package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.app.App;
import com.hakagamesstudio.begreen.pojos.pages_model.PagesData;
import com.hakagamesstudio.begreen.pojos.pages_model.PagesDetails;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.support.ConstantValues;
import com.hakagamesstudio.begreen.views.activities.AboutDescriptionActivity;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AboutFragment extends Fragment {

    @BindView(R.id.contentAbout)
    NestedScrollView content;

    private static final String OPEN = "WhereOpen";
    private String mOpenView;

    private App app;
    private CallBackRetrofit mCall = ApiUtils.getAPIService();
    private MaterialDialog mProgress;
    private Context mContext;
    private View rootView;

    public AboutFragment() {
    }


    public static AboutFragment newInstance(String open) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(OPEN, open);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) mContext = getContext();
        if (getArguments() != null){
            mOpenView = getArguments().getString(OPEN);
        }
        mProgress = BeGreenSupport.getInstance().createProgressDialog("Obteniendo datos...", mContext);
        mProgress.show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, rootView);
        app = ((App) mContext.getApplicationContext());

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
        RequestStaticPagesData();

        return rootView;
    }

    private void RequestStaticPagesData() {

        ConstantValues.ABOUT_US = app.getString(R.string.lorem_ipsum);
        ConstantValues.TERMS_SERVICES = app.getString(R.string.lorem_ipsum);
        ConstantValues.PRIVACY_POLICY = app.getString(R.string.lorem_ipsum);
        ConstantValues.REFUND_POLICY = app.getString(R.string.lorem_ipsum);


        Call<PagesData> call = mCall.getStaticPages(1);
        call.enqueue(new Callback<PagesData>() {
            @Override
            public void onResponse(Call<PagesData> call, Response<PagesData> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {

                            app.setStaticPagesDetails(response.body().getPagesData());

                            for (int i = 0; i < response.body().getPagesData().size(); i++) {
                                PagesDetails page = response.body().getPagesData().get(i);

                                if (page.getSlug().equalsIgnoreCase("about-us")) {
                                    ConstantValues.ABOUT_US = page.getDescription();
                                } else if (page.getSlug().equalsIgnoreCase("term-services")) {
                                    ConstantValues.TERMS_SERVICES = page.getDescription();
                                } else if (page.getSlug().equalsIgnoreCase("privacy-policy")) {
                                    ConstantValues.PRIVACY_POLICY = page.getDescription();
                                } else if (page.getSlug().equalsIgnoreCase("refund-policy")) {
                                    ConstantValues.REFUND_POLICY = page.getDescription();
                                }
                            }
                            createOptionsFragment(rootView);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<PagesData> call, Throwable t) {

            }
        });
    }


    void createOptionsFragment(View rootView){
        mProgress.dismiss();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Acerca de");

        TextView official_web = rootView.findViewById(R.id.official_web);
        TextView privacy_policy = rootView.findViewById(R.id.privacy_policy);
        TextView refund_policy = rootView.findViewById(R.id.refund_policy);
        TextView service_terms = rootView.findViewById(R.id.service_terms);
        WebView about_us_webView = rootView.findViewById(R.id.about_us_webView);

        String description = ConstantValues.ABOUT_US;
        String styleSheet = "<style> " +
                "body{background:#eeeeee; margin:0; padding:0} " +
                "p{color:#757575;} " +
                "img{display:inline; height:auto; max-width:100%;}" +
                "</style>";

        about_us_webView.setHorizontalScrollBarEnabled(false);
        about_us_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);

        official_web.setOnClickListener(v -> {
            String web_url = BeGreenSupport.getInstance().getAppSettingsDetails().getSiteUrl();
            if (!web_url.startsWith("https://")  &&  !web_url.startsWith("http://"))
                web_url = "http://" + web_url;

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web_url)));
        });

        privacy_policy.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AboutDescriptionActivity.class);
            intent.putExtra("title", "Politicas de privacidad");
            mContext.startActivity(intent);
        });

        refund_policy.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AboutDescriptionActivity.class);
            intent.putExtra("title", "Politicas de reembolso");
            mContext.startActivity(intent);
        });

        service_terms.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AboutDescriptionActivity.class);
            intent.putExtra("title", "Terminos de servicio");
            mContext.startActivity(intent);
        });

        if (mOpenView.equals("openPolicy")){
            Intent intent = new Intent(mContext, AboutDescriptionActivity.class);
            intent.putExtra("title", "Politicas de privacidad");
            intent.putExtra("open", "direct");
            mContext.startActivity(intent);
        }else {
            content.setVisibility(View.VISIBLE);
        }
    }
}