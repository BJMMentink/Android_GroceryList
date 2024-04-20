package com.example.grocerylist;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.ListViewHolder> {
    private ArrayList<GroceryItem> groceryData;
    private View.OnClickListener onItemClickListener;
    private boolean isShoppingList;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    public static final String TAG = "GroceryListAdapter";
    public ImageView imagePhoto;

    private Context parentContext;
    public class ListViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cbxShoppingCart;
        public TextView tvDescription;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            cbxShoppingCart = itemView.findViewById(R.id.cbxShoppingCart);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imagePhoto = itemView.findViewById(R.id.imgItem);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
            cbxShoppingCart.setTag(this);
            cbxShoppingCart.setOnCheckedChangeListener(onCheckedChangeListener);
            Log.d(TAG, "ListViewHolder: " + imagePhoto);
        }
        public CheckBox getCbxShoppingCart() { return cbxShoppingCart; }
        public TextView getTvDescription() { return tvDescription; }
        public ImageView getImagePhoto() {return imagePhoto;}

    }


    // Constructor to initialize the adapter with groceryItems, isShoppingList, and onCheckedChangeListener
    public GroceryListAdapter(ArrayList<GroceryItem> groceryItems, boolean isShoppingList) {
        this.groceryData = groceryItems;
        this.isShoppingList = isShoppingList;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grocery, parent, false);
        return new ListViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ListViewHolder listViewHolder = (ListViewHolder) holder;
        GroceryItem item = groceryData.get(position);
        listViewHolder.getTvDescription().setText(groceryData.get(position).getDescription());
        //listViewHolder.cbxShoppingCart.setOnCheckedChangeListener(onCheckedChangeListener);
        listViewHolder.getImagePhoto().setImageBitmap(groceryData.get(position).getPhoto());
        if (MainActivity.isShoppingList) {
            listViewHolder.getCbxShoppingCart().setChecked(groceryData.get(position).isInCart());
        } else {
            listViewHolder.getCbxShoppingCart().setChecked(groceryData.get(position).isOnShoppingList());
        }
        listViewHolder.cbxShoppingCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                onCheckedChangeListener.onCheckedChanged(buttonView, isChecked);

            }
        });
    }

    private void deleteItem(int position) {
        Log.d(TAG, "deleteItem: " + position);
        GroceryItem groceryItem = groceryData.get(position);
        groceryData.remove(position);
        Log.d(TAG, "deleteItem: parentContext: " + parentContext);
        GroceryListDataSource ds = new GroceryListDataSource(parentContext);
        Log.d(TAG, "deleteItem: " + groceryItem.toString());
        boolean didDelete = ds.delete(groceryItem) > 0;
        Log.d(TAG, "deleteItem: " + didDelete);
        notifyDataSetChanged();

    }
    public void setOnItemCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener)
    {
        Log.d(TAG, "setOnItemCheckedChangeListener: ");
        onCheckedChangeListener = listener;
    }


    @Override
    public int getItemCount() {
        return groceryData.size();
    }
}
