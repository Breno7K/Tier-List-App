package com.example.tier_list_app.activities.tier_list;

import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TierListAdapter extends RecyclerView.Adapter<TierListAdapter.TierViewHolder> {
    private List<Tier> tierList;
    private ItemListAdapter.OnAddItemClickListener onAddItemClickListener;

    public TierListAdapter(List<Tier> tierList, ItemListAdapter.OnAddItemClickListener onAddItemClickListener) {
        this.tierList = tierList != null ? tierList : new ArrayList<>();
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
        String tierId = tier.getId();

        holder.tierNameTextView.setText(tier.getName());

        FirebaseFirestore.getInstance().collection("item")
                .whereEqualTo("tierId", tierId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Item> itemList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Item item = document.toObject(Item.class);
                                itemList.add(item);
                            }

                            // Check if the bound tier still matches the current tier ID
                            if (tier.getId().equals(tierId)) {
                                ItemListAdapter itemListAdapter = new ItemListAdapter(itemList, onAddItemClickListener, tierId);
                                holder.itemRecyclerView.setAdapter(itemListAdapter);
                            }
                        } else {
                            Log.e("TierListAdapter", "Error fetching items from Firestore: ", task.getException());
                        }
                    }
                });

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.itemRecyclerView.setLayoutManager(layoutManager);

        holder.btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAddItemClickListener != null) {
                    onAddItemClickListener.onAddItemClick(holder.itemView.getContext(), tierId);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return tierList.size();
    }

    public void updateTierList(List<Tier> updatedTierList) {
        tierList.clear();
        if (updatedTierList != null) {
            tierList.addAll(updatedTierList);
        }
        notifyDataSetChanged();
    }

    public static class TierViewHolder extends RecyclerView.ViewHolder {
        public View btnAddItem;
        TextView tierNameTextView;
        RecyclerView itemRecyclerView;

        public TierViewHolder(@NonNull View itemView) {
            super(itemView);
            tierNameTextView = itemView.findViewById(R.id.tierNameTextView);
            itemRecyclerView = itemView.findViewById(R.id.itemRecyclerView);
            btnAddItem = itemView.findViewById(R.id.btnAddItem);
        }
    }
}
