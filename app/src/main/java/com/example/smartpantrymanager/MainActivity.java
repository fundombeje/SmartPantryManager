package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.RecipeSeeder;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PantryAdapter adapter;
    private TextView textEmpty;

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
    }

    /** Reload the screen. */
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
                textEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}