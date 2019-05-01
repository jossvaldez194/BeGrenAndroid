package com.hakagamesstudio.begreen.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.OrchardFavorites;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WhishListOrchardsAdapter extends RecyclerView.Adapter<WhishListOrchardsAdapter.WhishListOrcharViewHolder> {

    private ArrayList<OrchardFavorites> mListItems;
    private Context mContext;
    private View.OnClickListener mClick;

    public WhishListOrchardsAdapter(ArrayList<OrchardFavorites> mListItems, Context mContext, View.OnClickListener mClick) {
        this.mListItems = mListItems;
        this.mContext = mContext;
        this.mClick = mClick;

    }

    @NonNull
    @Override
    public WhishListOrcharViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_orchard, parent, false);
        return new WhishListOrcharViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WhishListOrcharViewHolder holder, int position) {
        OrchardFavorites favorites = mListItems.get(position);
        holder.txtNamae.setText(favorites.getOrchards_name());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.mipmap.logo_begreen)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(mContext)
                .load(favorites.getOrchards_image())
                .apply(options)
                .into(holder.imgPhoto);

        holder.imgDelete.setTag(favorites);
    }

    public void removeAt(int position) {
        mListItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mListItems.size());
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    class WhishListOrcharViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgDeleteWishOrchard)
        ImageButton imgDelete;
        @BindView(R.id.imgPhotoWishOrchard)
        ImageView imgPhoto;
        @BindView(R.id.imgMoreFromWishOrchard)
        ImageView imgMore;
        @BindView(R.id.txtNamaeWishOrchard)
        TextView txtNamae;

        WhishListOrcharViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imgDelete.setOnClickListener(mClick);
        }
    }
}
