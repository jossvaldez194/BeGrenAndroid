package com.hakagamesstudio.begreen.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.category_model.CategoryDetails;
import com.hakagamesstudio.begreen.support.ConstantValues;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.MenuCategoryViewHolder> implements Filterable {

    private List<CategoryDetails> mDataSourceCategory;
    private List<CategoryDetails> mDataSourceCategoryFilter;
    private View.OnClickListener mClickListener;
    private Context mContext;

    public MenuCategoryAdapter(List<CategoryDetails> mDataSourceCategory, View.OnClickListener mClickListener, Context mContext) {
        this.mDataSourceCategory = mDataSourceCategory;
        this.mDataSourceCategoryFilter = mDataSourceCategory;
        this.mClickListener = mClickListener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MenuCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_menu, parent, false);
        return new MenuCategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuCategoryViewHolder holder, int position) {
        CategoryDetails dataSource = mDataSourceCategoryFilter.get(position);
        holder.CategotyTitle.setText(dataSource.getName());
        holder.contentCategory.setTag(dataSource);
        Glide.with(mContext)
                .load(ConstantValues.ECOMMERCE_URL + dataSource.getImage())
                .into(holder.imgIconCategory);

    }

    @Override
    public int getItemCount() {
        return mDataSourceCategoryFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mDataSourceCategoryFilter = mDataSourceCategory;
                } else {
                    List<CategoryDetails> filteredList = new ArrayList<>();
                    for (CategoryDetails row : mDataSourceCategory) {
                        // name match condition. this might differ depending on your requirement
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mDataSourceCategoryFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataSourceCategoryFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataSourceCategoryFilter = (ArrayList<CategoryDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
        //return null;
    }

    class MenuCategoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_card)
        RelativeLayout contentCategory;
        @BindView(R.id.category_image)
        ImageView imgIconCategory;
        @BindView(R.id.category_title)
        TextView CategotyTitle;

        MenuCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            contentCategory.setOnClickListener(mClickListener);
        }
    }
}
