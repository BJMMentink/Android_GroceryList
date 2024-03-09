package com.example.grocerylist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.ListViewHolder> {
public static final String TAG = "GroceryListAdapter";
    private ArrayList<GroceryItem> groceryItems;
    private View.OnClickListener onItemClickListener;
    private Context parentContext;

    public class ListViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cbxShoppingCart;
        public TextView tvDescription;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            cbxShoppingCart = itemView.findViewById(R.id.cbxShoppingCart);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }

        public TextView gettvDescription() {
            return tvDescription;
        }

        public CheckBox getcbxShoppingCart() {
            return cbxShoppingCart;
        }
    }

    public GroceryListAdapter(ArrayList<GroceryItem> data, Context context) {
        groceryItems = data;
        Log.d(TAG, "GroceryListAdapter: " + data.size());
        parentContext = context;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        onItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grocery, parent, false);
        return new ListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        GroceryItem item = groceryItems.get(position);
        holder.getcbxShoppingCart().setChecked(item.isOnShoppingList());
        holder.gettvDescription().setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public void updateList(ArrayList<GroceryItem> newList) {
        groceryItems.addAll(newList);
        notifyDataSetChanged();
    }


}
