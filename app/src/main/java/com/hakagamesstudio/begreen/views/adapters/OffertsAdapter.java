package com.hakagamesstudio.begreen.views.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.infoOfferts;
import com.hakagamesstudio.begreen.support.ConstantValues;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OffertsAdapter extends RecyclerView.Adapter<OffertsAdapter.OffertsAdapterViewHolder> {

    private List<infoOfferts> mListOfferts;
    private Context mContext;

    public OffertsAdapter(List<infoOfferts> mListOfferts, Context mContext) {
        this.mListOfferts = mListOfferts;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public OffertsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offerts, parent, false);
        return new OffertsAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OffertsAdapterViewHolder holder, int position) {
        infoOfferts offerts = mListOfferts.get(position);
        String typeOffert = String.valueOf(offerts.getAmount());
        switch (offerts.getDiscount_type()) {
            case "percent_product":
                typeOffert = typeOffert + " % DE DESCUENTO EN PRODUCTO";
                break;

            case "percent":
                typeOffert = typeOffert + " % DE DESCUENTO EN TODA TU COMPRA";
                break;

            case "fixed_cart":
                typeOffert = "$" + typeOffert + " PESOS DE DESCUENTO EN PRODUCTO";
                break;

            case "fixed_product":
                typeOffert = "$" + typeOffert + " PESOS DE DESCUENTO EN TODA TU COMPRA";
                break;
        }

        switch (stringToDate(offerts.getDate_modified())) {
            case 1:
                holder.imglimitDateofferts.setBackgroundResource(R.drawable.ic_access_time_white_24dp);
                holder.txtCodeCoupond.setBackgroundResource(R.drawable.rounded_textview_coupons_orange);
                break;

            case 2:
                holder.imglimitDateofferts.setBackgroundResource(R.drawable.ic_star_white_24dp);
                break;

            case 3:
                holder.imglimitDateofferts.setBackgroundResource(R.drawable.ic_access_time_white_24dp);
                holder.txtCodeCoupond.setBackgroundResource(R.drawable.rounded_textview_coupons_orange);
                break;

            default:
                holder.imglimitDateofferts.setBackgroundResource(R.drawable.ic_star_white_24dp);
                break;
        }
        holder.txtCodeCoupond.setText(offerts.getCode());
        holder.txtTypeDiscount.setText(typeOffert);
        holder.txtOrchardDescriptionName.setText(offerts.getDescription());

        //stringToDate("2019-04-20 16:26:51");
        Log.i("Imagen", offerts.getPhoto());
        Glide.with(mContext)
                .load(ConstantValues.ECOMMERCE_URL + offerts.getPhoto())
                .into(holder.imgOfferts);
    }

    @Override
    public int getItemCount() {
        return mListOfferts.size();
    }

    private int stringToDate(String date) {
        int isToday = 0; //No mostrara nada porque ocurrio un error
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date dateCoupon = null;
        Date dateNow = new Date(System.currentTimeMillis());

        try {
            if (date != null) {
                dateCoupon = dateFormat.parse(date);
            }else{
                return isToday;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dateCoupon != null) {
            if (dateNow.compareTo(dateCoupon) >= 0) {
                isToday = 1;//Fecha del cupon inferior a hoy
            } else if (dateNow.compareTo(dateCoupon) == 0) {
                isToday = 2; //Mismos dia
            } else if (dateNow.compareTo(dateCoupon) <= 0) {
                isToday = 3; //Fecha del cupon superiores a hoy
            }
        }
        return isToday;
    }

    public class OffertsAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imfOfferts_adapter)
        ImageView imgOfferts;
        @BindView(R.id.imglimitDateofferts)
        ImageView imglimitDateofferts;
        @BindView(R.id.txtCodeCoupond)
        TextView txtCodeCoupond;
        @BindView(R.id.txtTypeDiscount)
        TextView txtTypeDiscount;
        @BindView(R.id.txtOrchardDescriptionName)
        TextView txtOrchardDescriptionName;

        public OffertsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
