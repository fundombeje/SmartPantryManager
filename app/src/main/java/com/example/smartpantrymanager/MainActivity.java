package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.RecipeSeeder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PantryAdapter adapter;
    private TextView textEmpty;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Seed the starter recipes on first run
        AppDatabase.databaseExecutor.execute(() ->
                RecipeSeeder.seedIfEmpty(AppDatabase.getInstance(getApplicationContext())));

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textEmpty = findViewById(R.id.textEmpty);

        RecyclerView recyclerPantry = findViewById(R.id.recyclerPantry);
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter();
        recyclerPantry.setAdapter(adapter);

        // Tapping a row opens the form in edit mode, passing the item's id in the Intent
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, AddEditItemActivity.class);
            intent.putExtra(AddEditItemActivity.EXTRA_ITEM_ID, item.id);
            startActivity(intent);
        });

        // The + button opens the same form with no id, meaning "add new"
        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditItemActivity.class)));

        setUpSwipeToDelete(recyclerPantry);
    }

    /** Reload every time the screen comes back to the foreground, so the list is never stale. */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    /** Reads the pantry on a background thread, then updates the UI on the main thread. */
    private void loadPantry() {
        AppDatabase.databaseExecutor.execute(() -> {
            List<PantryItem> items = AppDatabase.getInstance(getApplicationContext())
                    .pantryItemDao().getAll();
            runOnUiThread(() -> {
                adapter.setItems(items);
                updateEmptyState();
            });
        });
    }

    /** Shows the "pantry is empty" message only when there are no rows. */
    private void updateEmptyState() {
        textEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    /** Lets the user swipe a row left or right to delete it. */
    private void setUpSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    deleteItemAt(position);
                }
            }
        };
        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);
    }

    /** Removes the row immediately, deletes it from the database, and offers an Undo. */
    private void deleteItemAt(int position) {
        PantryItem item = adapter.getItemAt(position);
        adapter.removeItem(position);
        updateEmptyState();

        AppDatabase.databaseExecutor.execute(() ->
                AppDatabase.getInstance(getApplicationContext())
                        .pantryItemDao().delete(item)); // Delete

        Snackbar.make(findViewById(R.id.main), item.name + " deleted", Snackbar.LENGTH_LONG)
                .setAnchorView(fabAdd)
                .setAction("UNDO", v -> restoreItem(item))
                .show();
    }

    /** Undo: puts the deleted item back in the database with same id, then reloads the list. */
    private void restoreItem(PantryItem item) {
        AppDatabase.databaseExecutor.execute(() -> {
            AppDatabase.getInstance(getApplicationContext()).pantryItemDao().insert(item);
            loadPantry();
        });
    }
}