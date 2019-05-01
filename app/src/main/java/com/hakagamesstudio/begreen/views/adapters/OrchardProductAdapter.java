package com.hakagamesstudio.begreen.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.OrchardsProductSingle;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrchardProductAdapter extends RecyclerView.Adapter {

    private List<OrchardsProductSingle> dataSet;

    public OrchardProductAdapter(List<OrchardsProductSingle> data) {
        this.dataSet = data;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameProduct;

        CategoryViewHolder(View itemView) {
            super(itemView);
            this.txtNameProduct = itemView.findViewById(R.id.txtNameProduct_OrchardsAdapter);
        }
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameCategory;

        ProductsViewHolder(View itemView) {
            super(itemView);
            this.txtNameCategory = itemView.findViewById(R.id.txtNameCategory_OrchardsAdapter);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case OrchardsProductSingle.CATEGORY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_orchards_adapter, parent, false);
                return new ProductsViewHolder(view);
            case OrchardsProductSingle.PRODUCT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_orchards_adapter, parent, false);
                return new CategoryViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (dataSet.get(position).getType()) {
            case 0:
                return OrchardsProductSingle.CATEGORY;
            case 1:
                return OrchardsProductSingle.PRODUCT;
            default:
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int listPosition) {
        OrchardsProductSingle object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.getType()) {
                case OrchardsProductSingle.CATEGORY:
                    ((ProductsViewHolder) holder).txtNameCategory.setText(object.getTitleCatery());
                    break;

                case OrchardsProductSingle.PRODUCT:
                    ((CategoryViewHolder) holder).txtNameProduct.setText(object.getTitleProduct());
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
