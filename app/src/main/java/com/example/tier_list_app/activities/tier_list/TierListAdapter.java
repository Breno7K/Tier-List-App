package com.example.tier_list_app.activities.tier_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tier_list_app.R;
import com.example.tier_list_app.model.Item;
import com.example.tier_list_app.model.Tier;

import java.util.ArrayList;
import java.util.List;

public class TierListAdapter extends RecyclerView.Adapter<TierListAdapter.TierViewHolder> {
    private List<Tier> tierList;

    private ItemListAdapter.OnAddItemClickListener onAddItemClickListener;


    public TierListAdapter(List<Tier> tierList, ItemListAdapter.OnAddItemClickListener onAddItemClickListener) {
        this.tierList = tierList;
        this.onAddItemClickListener = onAddItemClickListener;
    }

    @NonNull
    @Override
    public TierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tier_level_list, parent, false);
        return new TierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TierViewHolder holder, int position) {
        Tier tier = tierList.get(position);
        holder.tierNameTextView.setText(tier.getName());

        ArrayList<Item> itemList = tier.getItens();
        ItemListAdapter itemListAdapter = new ItemListAdapter(itemList, onAddItemClickListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.itemRecyclerView.setLayoutManager(layoutManager);
        holder.itemRecyclerView.setAdapter(itemListAdapter);
    }


    @Override
    public int getItemCount() {
        return tierList != null ? tierList.size() : 0;
    }

    public void updateTierList(List<Tier> updatedTierList) {
        tierList.clear();
        if (updatedTierList != null) {
            tierList.addAll(updatedTierList);
        }
        notifyDataSetChanged();
    }

    public static class TierViewHolder extends RecyclerView.ViewHolder {
        TextView tierNameTextView;
        RecyclerView itemRecyclerView;

        public TierViewHolder(@NonNull View itemView) {
            super(itemView);
            tierNameTextView = itemView.findViewById(R.id.tierNameTextView);
            itemRecyclerView = itemView.findViewById(R.id.itemRecyclerView);
        }
    }
}
