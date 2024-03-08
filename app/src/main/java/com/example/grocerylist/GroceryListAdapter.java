package com.example.grocerylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.GroceryViewHolder> {

    private ArrayList<GroceryItem> groceryItems;
    private OnItemClickListener onItemClickListener;

    public GroceryListAdapter(ArrayList<GroceryItem> groceryItems) {
        this.groceryItems = groceryItems;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grocery, parent, false);
        return new GroceryViewHolder(view, onItemClickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        GroceryItem groceryItem = groceryItems.get(position);
        holder.descriptionTextView.setText(groceryItem.getDescription());
        if (groceryItem.isOnShoppingList()) {
            holder.shoppingCheckBox.setVisibility(View.VISIBLE);
            holder.shoppingCheckBox.setChecked(true);
        } else {
            holder.shoppingCheckBox.setVisibility(View.INVISIBLE);
        }
        holder.shoppingCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            groceryItem.setOnShoppingList(isChecked);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class GroceryViewHolder extends RecyclerView.ViewHolder {

        TextView descriptionTextView;
        CheckBox shoppingCheckBox;

        OnItemClickListener onItemClickListener;

        public GroceryViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.tvDescription);
            shoppingCheckBox = itemView.findViewById(R.id.cbxShoppingCart);

            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, position);
                    }
                }
            });
        }
    }
}