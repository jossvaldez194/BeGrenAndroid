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
import com.hakagamesstudio.begreen.pojos.Offerts;
import com.hakagamesstudio.begreen.pojos.news_model.all_news.NewsDetails;
import com.hakagamesstudio.begreen.support.ConstantValues;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class listOrchardSingleProduct extends RecyclerView.Adapter<listOrchardSingleProduct.listOrchardSingleProductViewHolder> {

    private List<NewsDetails> newsList;
    private View.OnClickListener mClickListener;
    private Context mContext;
    private int row_index = 0;

    public listOrchardSingleProduct(List<NewsDetails> newsList, View.OnClickListener mClickListener, Context mContext) {
        this.newsList = newsList;
        this.mClickListener = mClickListener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public listOrchardSingleProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_orchard_single_product, parent, false);
        return new listOrchardSingleProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull listOrchardSingleProductViewHolder holder, int position) {
        NewsDetails offerts = newsList.get(position);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.mipmap.logo_begreen)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(mContext)
                .load(ConstantValues.ECOMMERCE_URL + offerts.getNewsImage())
                .apply(options)
                .into(holder.imgLogo);
        holder.txtName.setText(offerts.getNewsName());
        holder.selectedLayout.setTag(offerts);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class listOrchardSingleProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgLogo_seingleProduct)
        ImageView imgLogo;
        @BindView(R.id.txtNameOrchard_singleProduct)
        TextView txtName;
        @BindView(R.id.select_orchard_dialog)
        ConstraintLayout selectedLayout;

        public listOrchardSingleProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            selectedLayout.setOnClickListener(mClickListener);

        }
    }
}
