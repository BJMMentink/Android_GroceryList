package com.example.grocerylist;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.ListViewHolder> {
    private ArrayList<GroceryItem> groceryItems;
    private boolean isShoppingList;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener; // Define onCheckedChangeListener variable

    // Constructor to initialize the adapter with groceryItems, isShoppingList, and onCheckedChangeListener
    public GroceryListAdapter(ArrayList<GroceryItem> groceryItems, boolean isShoppingList, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.groceryItems = groceryItems;
        this.isShoppingList = isShoppingList;
        this.onCheckedChangeListener = onCheckedChangeListener;
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
        holder.cbxShoppingCart.setText(item.getDescription());
        holder.cbxShoppingCart.setTag(position);
        holder.cbxShoppingCart.setOnCheckedChangeListener(onCheckedChangeListener);
        if (MainActivity.isShoppingList) {
            if (item.isInCart()){
                holder.cbxShoppingCart.setChecked(true);
            }
            else{
                holder.cbxShoppingCart.setChecked(false);
            }
        } else {
            if (item.isOnShoppingList()){
                holder.cbxShoppingCart.setChecked(true);
            }
            else{
                holder.cbxShoppingCart.setChecked(false);
            }
        }
    }




    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cbxShoppingCart;
        public TextView tvDescription;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            cbxShoppingCart = itemView.findViewById(R.id.cbxShoppingCart);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
