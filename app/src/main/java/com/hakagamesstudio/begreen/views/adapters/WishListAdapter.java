package com.hakagamesstudio.begreen.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.product_model.ProductDetails;
import com.hakagamesstudio.begreen.support.ConstantValues;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.WishListAdapterViewHolder> {

    private List<ProductDetails> favouriteProductsList;
    private Context mContext;
    private View.OnClickListener mClickListener;

    public WishListAdapter(List<ProductDetails> favouriteProductsList, Context mContext, View.OnClickListener mClickListener) {
        this.favouriteProductsList = favouriteProductsList;
        this.mContext = mContext;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public WishListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wish_list, parent, false);
        return new WishListAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WishListAdapterViewHolder holder, int position) {
        ProductDetails details = favouriteProductsList.get(position);
        holder.deleteWish.setTag(details);
        holder.addToCart.setTag(details);
        holder.txtNameWish.setTag(details);
        Glide.with(mContext)
                .load(ConstantValues.ECOMMERCE_URL + details.getProductsImage())
                .into(holder.photoWhish);
        holder.txtNameWish.setText(details.getProductsName());
    }

    @Override
    public int getItemCount() {
        return favouriteProductsList.size();
    }

    public void removeAt(int position) {
        favouriteProductsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, favouriteProductsList.size());
    }

    class WishListAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgDeleteWish)
        ImageButton deleteWish;
        @BindView(R.id.imgPhotoWish)
        ImageView photoWhish;
        @BindView(R.id.imgAddFromWish)
        ImageView addToCart;
        @BindView(R.id.txtNamaeWish)
        TextView txtNameWish;
        @BindView(R.id.contentWishList)
        ConstraintLayout contentList;

        WishListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            contentList.setOnClickListener(mClickListener);

            addToCart.setOnClickListener(mClickListener);
            deleteWish.setOnClickListener(mClickListener);
        }
    }
}
