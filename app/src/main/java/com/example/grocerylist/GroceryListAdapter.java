package com.example.grocerylist;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.ListViewHolder> {
    private ArrayList<GroceryItem> groceryItems;
    private View.OnClickListener onItemClickListener;
    private CheckBox.OnCheckedChangeListener onCheckedChangeListener;

    public GroceryListAdapter(ArrayList<GroceryItem> data, Context context, CheckBox.OnCheckedChangeListener onCheckedChangeListener) {
        groceryItems = data;
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
        holder.cbxShoppingCart.setChecked(item.isOnShoppingList());
        holder.tvDescription.setText(item.getDescription());

        // Set the tag to the position of the item to track its position
        holder.cbxShoppingCart.setTag(position);
        // Set the onCheckedChangeListener for the checkbox
        holder.cbxShoppingCart.setOnCheckedChangeListener(onCheckedChangeListener);
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
