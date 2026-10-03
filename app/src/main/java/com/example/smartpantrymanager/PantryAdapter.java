package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.PantryItem;

import java.util.ArrayList;
import java.util.List;

/** Connects the list of PantryItem objects from the database to the RecyclerView rows. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /** Lets the Activity react when a row is tapped. */
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /** Replaces the displayed list and redraws it. */
    public void setItems(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    /** Called to fill a row with the data for one position. */
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.textName.setText(item.name);
        holder.textQuantity.setText(formatQuantity(item.quantity) + " " + item.unit);

        // Always set visibility both ways, because rows are recycled.
        if (item.expiryDate == null || item.expiryDate.isEmpty()) {
            holder.textExpiry.setVisibility(View.GONE);
        } else {
            holder.textExpiry.setVisibility(View.VISIBLE);
            holder.textExpiry.setText("Expires: " + item.expiryDate);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static String formatQuantity(double quantity) {
        if (quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textQuantity;
        final TextView textExpiry;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textExpiry = itemView.findViewById(R.id.textExpiry);
        }
    }
}