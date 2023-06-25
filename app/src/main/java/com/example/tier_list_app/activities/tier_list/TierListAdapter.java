package com.example.tier_list_app.activities.tier_list;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tier_list_app.R;
import com.example.tier_list_app.activities.tier_list.ItemListAdapter;
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
    private List<Tier> listOfTiers;
    private ItemListAdapter.OnAddItemClickListener onAddItemClickListener;

    public TierListAdapter(List<Tier> tierList, ItemListAdapter.OnAddItemClickListener onAddItemClickListener) {
        this.listOfTiers = tierList != null ? tierList : new ArrayList<>();
        this.onAddItemClickListener = onAddItemClickListener;
    }

    public interface OnAddItemClickListener {
        void onAddItemClick(Context context, String tierId);
    }

    public void setOnAddItemClickListener(ItemListAdapter.OnAddItemClickListener listener) {
        this.onAddItemClickListener = listener;
    }


    @NonNull
    @Override
    public TierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tier_level_list, parent, false);
        return new TierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TierViewHolder holder, int position) {
        Tier tier = listOfTiers.get(position);
        String tierId = tier.getId();

        holder.tierNameTextView.setText(tier.getName());
        holder.tierNameTextView.setBackgroundColor(Color.parseColor(tier.getColor()));

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
                            if (tier.getId().equals(tierId)) {
                                ItemListAdapter itemListAdapter = new ItemListAdapter(itemList, onAddItemClickListener, tierId);
                                holder.itemRecyclerView.setAdapter(itemListAdapter);
                            }
                        } else {
                            Log.e("TierListAdapter", "Error fetching items from Firestore: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listOfTiers.size();
    }

    public void updateTierList(List<Tier> updatedTierList) {
        listOfTiers.clear();
        if (updatedTierList != null) {
            listOfTiers.addAll(updatedTierList);
        }
        notifyDataSetChanged();
    }

    // Retrieve the updated tier list based on your application's logic
    private List<Tier> retrieveUpdatedTierList() {
        // Replace this with your own logic to fetch the updated tier list
        // For example, you could query a database or update a local data source
        // Return the updated tier list
        return new ArrayList<>();
    }

    public class TierViewHolder extends RecyclerView.ViewHolder {
        public View btnAddItem;
        TextView tierNameTextView;
        RecyclerView itemRecyclerView;

        public TierViewHolder(@NonNull View itemView) {
            super(itemView);
            tierNameTextView = itemView.findViewById(R.id.tierNameTextView);
            itemRecyclerView = itemView.findViewById(R.id.itemRecyclerView);
            btnAddItem = itemView.findViewById(R.id.btnAddItem);
            itemRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            itemRecyclerView.setLayoutManager(layoutManager);

            btnAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onAddItemClickListener != null) {
                        Tier tier = listOfTiers.get(position);
                        String tierId = tier.getId();
                        onAddItemClickListener.onAddItemClick(itemView.getContext(), tierId);
                    }
                }
            });
        }
    }
}
