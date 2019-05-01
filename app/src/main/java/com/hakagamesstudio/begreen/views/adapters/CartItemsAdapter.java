package com.hakagamesstudio.begreen.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hakagamesstudio.begreen.CallBacks.CartListener;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.databases.User_Cart_DB;
import com.hakagamesstudio.begreen.databases.User_Recents_DB;
import com.hakagamesstudio.begreen.pojos.cart_model.CartProduct;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.support.ConstantValues;
import com.hakagamesstudio.begreen.views.fragments.MyCartFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.CartItemsAdapterViewHolder> {

    private List<CartProduct> cartItemsList;
    private User_Cart_DB user_cart_db;
    private User_Recents_DB recents_db;
    private Context mContext;
    private CartListener mListener;
    private float mServieSend;

    public CartItemsAdapter(List<CartProduct> cartItemsList, Context context, CartListener listener, float serviceSend) {
        this.cartItemsList = cartItemsList;
        this.mContext = context;
        this.mListener = listener;
        this.mServieSend = serviceSend;

        user_cart_db = new User_Cart_DB();
        recents_db = new User_Recents_DB();
    }

    @NonNull
    @Override
    public CartItemsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mycart_adapter, parent, false);

        return new CartItemsAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemsAdapterViewHolder holder, int position) {
        CartProduct data = cartItemsList.get(position);
        holder.imgDelete.setTag(data);
        holder.txtNombre.setText(data.getCustomersBasketProduct().getProductsName());
        holder.txtcategoria.setText(data.getCustomersBasketProduct().getCategoriesName());
        holder.txtCostoTotal.setText(BeGreenSupport.getInstance().moneyFormat(Float.parseFloat(data.getCustomersBasketProduct().getTotalPrice())));
        holder.txtCostoPieza.setText("Costo pieza ".concat(BeGreenSupport.getInstance().moneyFormat(Float.parseFloat(data.getCustomersBasketProduct().getProductsPrice()))));
        Glide.with(mContext).load(ConstantValues.ECOMMERCE_URL + data.getCustomersBasketProduct().getProductsImage()).into(holder.imgProduct);
        holder.txtCantidad.setText("".concat(String.valueOf(data.getCustomersBasketProduct().getCustomersBasketQuantity())));

        // Delete relevant Product from Cart
        holder.imgDelete.setOnClickListener(view -> {
            holder.imgDelete.setClickable(false);
            mListener.onDescountProduct(data.getCustomersBasketId());
            setCartTotal();
            cartItemsList.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            // Update Cart View from Local Database using static method of My_Cart
            //MyCartFragment.updateCartView(getItemCount());

        });

        // Holds Product Quantity
        final int[] number = {1};
        number[0] = data.getCustomersBasketProduct().getCustomersBasketQuantity();

        holder.imgRestar.setOnClickListener(view -> {
            // Check if the Quantity is greater than the minimum Quantity
            if (number[0] > 1){
                // Decrease Quantity by 1
                number[0] = number[0] - 1;
                holder.txtCantidad.setText(""+ number[0]);
                // Calculate Product Price with selected Quantity
                double price = Double.parseDouble(data.getCustomersBasketProduct().getProductsFinalPrice()) * number[0];
                // Set Final Price and Quantity
                data.getCustomersBasketProduct().setTotalPrice(""+ price);
                data.getCustomersBasketProduct().setCustomersBasketQuantity(number[0]);
                // Update CartItem in Local Database using static method of My_Cart
                MyCartFragment.UpdateCartItem(data);
                // Calculate Cart's Total Price Again
                setCartTotal();
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        // Increase Product Quantity
        holder.imgAgregar.setOnClickListener(view -> {
            // Check if the Quantity is less than the maximum or stock Quantity
            if (number[0] < data.getCustomersBasketProduct().getProductsQuantity()) {
                // Increase Quantity by 1
                number[0] = number[0] + 1;
                holder.txtCantidad.setText(""+ number[0]);
                // Calculate Product Price with selected Quantity
                double price = Double.parseDouble(data.getCustomersBasketProduct().getProductsFinalPrice()) * number[0];
                // Set Final Price and Quantity
                data.getCustomersBasketProduct().setTotalPrice(""+ price);
                data.getCustomersBasketProduct().setCustomersBasketQuantity(number[0]);
                // Update CartItem in Local Database using static method of My_Cart
                MyCartFragment.UpdateCartItem(data);
                // Calculate Cart's Total Price Again
                setCartTotal();
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemsList.size();
    }

    public void setCartTotal() {
        double finalPrice = 0;
        List<CartProduct> cartItemsList = user_cart_db.getCartItems();

        for (int i = 0; i < cartItemsList.size(); i++) {
            finalPrice += Double.parseDouble(cartItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
        }

        double finalPrecioEnvio = finalPrice + mServieSend;
        MyCartFragment.cart_total_price.setText(BeGreenSupport.getInstance().moneyFormat(Float.parseFloat(String.valueOf(finalPrecioEnvio))));
        MyCartFragment.txtSubtotal.setText(BeGreenSupport.getInstance().moneyFormat(Float.parseFloat(String.valueOf(finalPrice))));

    }

    class CartItemsAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.categoria_producto_mycart)
        TextView txtcategoria;
        @BindView(R.id.nombre_producto_mycart)
        TextView txtNombre;
        @BindView(R.id.txtPricePiece_mycart)
        TextView txtCostoPieza;
        @BindView(R.id.txtTotalMyCart)
        TextView txtCostoTotal;
        @BindView(R.id.txtQunatityProducts_mycart)
        TextView txtCantidad;
        @BindView(R.id.btnDeleteCartItem)
        ImageView imgDelete;
        @BindView(R.id.imageProductMyCartItem)
        ImageView imgProduct;
        @BindView(R.id.btnAddProducts_mycart)
        ImageButton imgAgregar;
        @BindView(R.id.btnLessProducts_mycart)
        ImageButton imgRestar;

        CartItemsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
           /* imgRestar.setOnClickListener(mClickListener);
            imgAgregar.setOnClickListener(mClickListener);*/

        }
    }
}
