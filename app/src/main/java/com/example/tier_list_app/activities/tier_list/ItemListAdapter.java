package com.example.tier_list_app.activities.tier_list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tier_list_app.R;
import com.example.tier_list_app.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_ADD_ITEM = 1;

    private List<Item> itemList;
    private OnAddItemClickListener onAddItemClickListener;
    private Context context;

    public ItemListAdapter(List<Item> itemList, OnAddItemClickListener onAddItemClickListener) {
        this.itemList = itemList != null ? itemList : new ArrayList<>();
        this.onAddItemClickListener = onAddItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.tier_item, parent, false);
        return new ViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.viewType == VIEW_TYPE_ITEM) {
            Item item = itemList.get(position);
            holder.itemNameTextView.setText(item.getName());
        } else if (holder.viewType == VIEW_TYPE_ADD_ITEM) {
            holder.btnAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAddItemClickListener != null) {
                        onAddItemClickListener.onAddItemClick(context);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == itemList.size()) {
            return VIEW_TYPE_ADD_ITEM;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        Button btnAddItem;
        int viewType;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == VIEW_TYPE_ITEM) {
                itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            } else if (viewType == VIEW_TYPE_ADD_ITEM) {
                btnAddItem = itemView.findViewById(R.id.btnAddItem);
            }
        }
    }

    public interface OnAddItemClickListener {
        void onAddItemClick(Context context);
    }
}
