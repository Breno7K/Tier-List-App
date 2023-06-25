package com.example.tier_list_app.activities.tier_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tier_list_app.R;
import com.example.tier_list_app.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Item> itemList;
    private OnAddItemClickListener onAddItemClickListener;
    private Context context;
    private String tierId;

    public ItemListAdapter(List<Item> itemList, OnAddItemClickListener onAddItemClickListener, String tierId) {
        this.itemList = itemList;
        this.onAddItemClickListener = onAddItemClickListener;
        this.tierId = tierId;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.tier_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            if (itemList != null && position < itemList.size()) {
                Item item = itemList.get(position);
                ((ItemViewHolder) holder).bind(item);
            }
        } else if (holder instanceof AddItemViewHolder) {
            AddItemViewHolder addItemViewHolder = (AddItemViewHolder) holder;
            addItemViewHolder.btnAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAddItemClickListener != null) {
                        onAddItemClickListener.onAddItemClick(context, tierId);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = itemList.size();
        return itemList.isEmpty() ? 0 : itemCount;
    }
    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
        }

        void bind(Item item) {
            Picasso.get().load(item.getImageUrl()).into(itemImageView);
        }
    }

    private static class AddItemViewHolder extends RecyclerView.ViewHolder {
        Button btnAddItem;

        AddItemViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAddItem = itemView.findViewById(R.id.btnAddItem);
        }
    }

    public interface OnAddItemClickListener {
        void onAddItemClick(Context context, String tierId);
    }
}
