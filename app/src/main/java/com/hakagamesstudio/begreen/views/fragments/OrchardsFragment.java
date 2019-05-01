package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.customs.EndlessRecyclerViewScroll;
import com.hakagamesstudio.begreen.pojos.news_model.all_news.NewsData;
import com.hakagamesstudio.begreen.pojos.news_model.all_news.NewsDetails;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.views.adapters.OrchardsAdapter;
import com.google.android.material.snackbar.Snackbar;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrchardsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.news_recycler)
    RecyclerView news_recycler;
    @BindView(R.id.loading_bar)
    ProgressBar progressBar;

    private CallBackRetrofit mCall = ApiUtils.getAPIService();
    private Unbinder mUnbinder;
    private View rootView;

    private Context mContext;
    private OrchardsAdapter newsAdapter;
    private List<NewsDetails> newsList;
    private Call<NewsData> mGetData;

    public OrchardsFragment() {

    }

    public OrchardsFragment newInstance() {
        OrchardsFragment fragment = new OrchardsFragment();
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
        rootView = inflater.inflate(R.layout.fragment_orchards, container, false);
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

        newsList = new ArrayList<>();
        int pageNo = 0;
        RequestAllNews(pageNo);

        newsAdapter = new OrchardsAdapter(newsList, this, mContext);
        news_recycler.setAdapter(newsAdapter);
        news_recycler.setLayoutManager(new LinearLayoutManager(mContext));

        news_recycler.addOnScrollListener(new EndlessRecyclerViewScroll() {
            // Override abstract method onLoadMore() of EndlessRecyclerViewScroll class
            @Override
            public void onLoadMore(int current_page) {
                progressBar.setVisibility(View.VISIBLE);
                // Execute AsyncTask LoadMoreTask to Load More Products from Server
                new LoadMoreTask(current_page).execute();
            }
        });

        newsAdapter.notifyDataSetChanged();
        return rootView;
    }

    private void addNews(NewsData newsData) {
        newsList.addAll(newsData.getNewsData());
        newsAdapter.notifyDataSetChanged();
    }


    private void RequestAllNews(int pageNumber) {
        mGetData = mCall.getAllNews(1, pageNumber, 0, null);
        mGetData.enqueue(new Callback<NewsData>() {
            @Override
            public void onResponse(Call<NewsData> call, Response<NewsData> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            addNews(response.body());
                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            addNews(response.body());
                            Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(rootView, "Imposible obtener datos de huertas", Snackbar.LENGTH_SHORT).show();
                        }
                        if (isAdded())
                            if (progressBar != null)
                                progressBar.setVisibility(View.GONE);
                    } else {
                        if (isAdded())
                            if (progressBar != null)
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isAdded())
                        if (progressBar != null)
                            Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsData> call, @NonNull Throwable t) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        NewsDetails newsDetails = (NewsDetails) view.getTag();
        if (getActivity() != null) {
            Fragment selected = new OrchardsDetailsFragment().newInstance(newsDetails);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.contentLayout, selected);
            transaction.commit();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGetData.cancel();
        mUnbinder.unbind();
    }

    private class LoadMoreTask extends AsyncTask<String, Void, String> {
        int page_number;

        private LoadMoreTask(int page_number) {
            this.page_number = page_number;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestAllNews(page_number);
            return "All Done!";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
