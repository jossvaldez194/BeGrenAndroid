package com.hakagamesstudio.begreen.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.pojos.category_model.CategoryDetails;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CatalogueProductFilterAdapterAux extends
        RecyclerView.Adapter<CatalogueProductFilterAdapterAux.CatalogueProductFilterAdapterAuxViewHolder> implements Filterable {

    private List<CategoryDetails> mListData;
    private List<CategoryDetails> mListDataFilter;
    private Context mContext;
    private View.OnClickListener mClickListener;

    public CatalogueProductFilterAdapterAux(List<CategoryDetails> mListData, Context mContext, View.OnClickListener mClickListener) {
        this.mListData = mListData;
        this.mListDataFilter = mListData;
        this.mContext = mContext;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public CatalogueProductFilterAdapterAux.CatalogueProductFilterAdapterAuxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_catalogue_products_adapter, parent, false);
        return new CatalogueProductFilterAdapterAuxViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueProductFilterAdapterAux.CatalogueProductFilterAdapterAuxViewHolder holder, int position) {
        CategoryDetails categoryDetails = mListDataFilter.get(position);
        holder.txtName.setText(categoryDetails.getName());
        holder.layoutContent.setTag(categoryDetails);
    }

    @Override
    public int getItemCount() {
        return mListDataFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mListDataFilter = mListData;
                } else {
                    List<CategoryDetails> filteredList = new ArrayList<>();
                    for (CategoryDetails row : mListData) {
                        // name match condition. this might differ depending on your requirement
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mListDataFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListDataFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mListDataFilter = (ArrayList<CategoryDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class CatalogueProductFilterAdapterAuxViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtNameCatalogueSearch)
        TextView txtName;
        @BindView(R.id.layoutProductAux)
        LinearLayout layoutContent;

        public CatalogueProductFilterAdapterAuxViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutContent.setOnClickListener(mClickListener);
        }
    }
}
