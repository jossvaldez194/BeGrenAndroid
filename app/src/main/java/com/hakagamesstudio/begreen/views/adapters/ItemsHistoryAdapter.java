package com.hakagamesstudio.begreen.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hakagamesstudio.begreen.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemsHistoryAdapter extends RecyclerView.Adapter<ItemsHistoryAdapter.ItemsViewHolder> {

    public ItemsHistoryAdapter() {
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_shop_adapter, parent, false);
        return new ItemsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtPriceShop)
        TextView txtPrice;
        @BindView(R.id.txtAddresShop)
        TextView txtAddress;
        @BindView(R.id.txtHourShop)
        TextView txtHour;
        @BindView(R.id.txtDateShop)
        TextView txtDate;
        @BindView(R.id.imgSendSingleShop)
        ImageView btnSendSingleShop;

        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
