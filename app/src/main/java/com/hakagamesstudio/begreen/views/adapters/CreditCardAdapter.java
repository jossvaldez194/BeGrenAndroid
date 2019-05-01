package com.hakagamesstudio.begreen.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.RoomDatabase.CreditCard;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.CreditCardAdapterViewHolder> {

    private List<com.hakagamesstudio.begreen.RoomDatabase.CreditCard> mListCreditCard;
    private View.OnClickListener mClickListener;

    public CreditCardAdapter(List<com.hakagamesstudio.begreen.RoomDatabase.CreditCard> mListCreditCard, View.OnClickListener mClickListener) {
        this.mListCreditCard = mListCreditCard;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public CreditCardAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_credit_card_adapter, parent, false);
        return new CreditCardAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditCardAdapterViewHolder holder, int position) {
        com.hakagamesstudio.begreen.RoomDatabase.CreditCard card = mListCreditCard.get(position);
        holder.btnEdit.setTag(card);
        holder.btnDeleteCard.setTag(card);
        holder.txtNameTarjetaHabiente.setText(card.getNameUser());
        holder.txtNumeroTarjeta.setText(card.getNumberTarjet());
    }

    @Override
    public int getItemCount() {
        return mListCreditCard.size();
    }

    public void updateListCard(List<CreditCard> list) {
        if (mListCreditCard != null) mListCreditCard = list;
        notifyDataSetChanged();
    }

    public class CreditCardAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgEditCreditCard)
        ImageView btnEdit;
        @BindView(R.id.imgDeleteCard_item)
        ImageView btnDeleteCard;
        @BindView(R.id.txtNameTarjetaHabiente)
        TextView txtNameTarjetaHabiente;
        @BindView(R.id.txtNumeroTarjeta_item)
        TextView txtNumeroTarjeta;

        public CreditCardAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnEdit.setOnClickListener(mClickListener);
            btnDeleteCard.setOnClickListener(mClickListener);
        }
    }
}
