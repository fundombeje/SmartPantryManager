package com.example.smartpantrymanager;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.logic.ExpiryHelper;

import java.util.ArrayList;
import java.util.List;

/** Connects the list of PantryItem objects from the database to the RecyclerView rows. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /** Lets the Activity react when a row is tapped (used for editing). */
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private static final int EXPIRING_COLOR = Color.parseColor("#D32F2F");

    private List<PantryItem> items = new ArrayList<>();
    private OnItemClickListener listener;
    private boolean highlightExpiring = true;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /** Turns the red "expiring soon" highlight on or off (comes from the Settings screen). */
    public void setHighlightExpiring(boolean enabled) {
        this.highlightExpiring = enabled;
    }

    /** Replaces the displayed list and redraws it. A copy is kept so rows can be removed. */
    public void setItems(List<PantryItem> newItems) {
        this.items = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    /** Returns the item shown at the given row position. */
    public PantryItem getItemAt(int position) {
        return items.get(position);
    }

    /** Removes one row from the list and animates it out. */
    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    /** Called when RecyclerView needs a new row view: inflates item_pantry.xml. */
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    /** Called to fill a (possibly recycled) row with the data for one position. */
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.textName.setText(item.name);
        holder.textQuantity.setText(formatQuantity(item.quantity) + " " + item.unit);

        // Always set visibility and colour both ways, because rows are recycled.
        if (item.expiryDate == null || item.expiryDate.isEmpty()) {
            holder.textExpiry.setVisibility(View.GONE);
        } else {
            holder.textExpiry.setVisibility(View.VISIBLE);

            String label = "Expires: " + item.expiryDate;
            int color = holder.defaultExpiryColor;

            if (highlightExpiring && ExpiryHelper.isExpiringSoon(item.expiryDate)) {
                boolean expired = ExpiryHelper.daysUntilExpiry(item.expiryDate) < 0;
                label += expired ? " (expired)" : " (soon)";
                color = EXPIRING_COLOR;
            }
            holder.textExpiry.setText(label);
            holder.textExpiry.setTextColor(color);
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

    /** Shows 500 instead of 500.0, but keeps 0.5 as 0.5. */
    static String formatQuantity(double quantity) {
        if (quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    /** Holds the views of one row so they are looked up only once. */
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textQuantity;
        final TextView textExpiry;
        final int defaultExpiryColor; // the normal text colour, restored when not highlighted

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textExpiry = itemView.findViewById(R.id.textExpiry);
            defaultExpiryColor = textExpiry.getCurrentTextColor();
        }
    }
}