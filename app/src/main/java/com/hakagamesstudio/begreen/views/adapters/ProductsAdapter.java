package com.hakagamesstudio.begreen.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.product_model.ProductDetails;
import com.hakagamesstudio.begreen.support.ConstantValues;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> implements Filterable {

    private List<ProductDetails> mDataSource;
    private List<ProductDetails> contactListFiltered;
    private Context mContext;
    private View.OnClickListener mClickListener;

    public ProductsAdapter(List<ProductDetails> mDataSource, Context context, View.OnClickListener mClickListener) {
        this.mDataSource = mDataSource;
        this.contactListFiltered = mDataSource;
        this.mContext = context;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_products, parent, false);
        return new ProductsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        ProductDetails data = contactListFiltered.get(position);
        holder.ctlContent.setTag(data);
        holder.txtNameProdcut.setText(data.getProductsName());
        Glide.with(mContext)
                .load(ConstantValues.ECOMMERCE_URL + data.getProductsImage())
                .into(holder.imgProductImage);
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = mDataSource;
                } else {
                    List<ProductDetails> filteredList = new ArrayList<>();
                    for (ProductDetails row : mDataSource) {
                        // name match condition. this might differ depending on your requirement
                        if (row.getProductsName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<ProductDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ProductsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtNameProduct)
        TextView txtNameProdcut;
        @BindView(R.id.imgProduct)
        ImageView imgProductImage;
        @BindView(R.id.contentProductLayout)
        ConstraintLayout ctlContent;

        ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ctlContent.setOnClickListener(mClickListener);
        }
    }
}
