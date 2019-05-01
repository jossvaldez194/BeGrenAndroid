package com.hakagamesstudio.begreen.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.news_model.all_news.NewsDetails;
import com.hakagamesstudio.begreen.support.ConstantValues;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrchardsAdapter extends RecyclerView.Adapter<OrchardsAdapter.OrchardsViewHolder> {


    private List<NewsDetails> mlistDataSource;
    private View.OnClickListener mCLcikListener;
    private Context mContext;

    public OrchardsAdapter(List<NewsDetails> mlistDataSource, View.OnClickListener mCLcikListener, Context mContext) {
        this.mlistDataSource = mlistDataSource;
        this.mCLcikListener = mCLcikListener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public OrchardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_orchards_adapter, parent, false);
        return new OrchardsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrchardsViewHolder holder, int position) {
        NewsDetails datasource = mlistDataSource.get(position);

        holder.txtNameOrchards.setText(datasource.getNewsName());
        holder.contentLayout.setTag(datasource);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.mipmap.logo_begreen)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(mContext)
                .load(ConstantValues.ECOMMERCE_URL + datasource.getNewsImage())
                .apply(options)
                .into(holder.imgOrchardsImage);

    }

    @Override
    public int getItemCount() {
        return mlistDataSource.size();
    }

    class OrchardsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgImageOrchards_adapter)
        ImageView imgOrchardsImage;
        @BindView(R.id.txtNameOrcharsd_adapter)
        TextView txtNameOrchards;
        @BindView(R.id.contentOrchardsList_adapter)
        ConstraintLayout contentLayout;

        OrchardsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            contentLayout.setOnClickListener(mCLcikListener);
        }
    }
}
