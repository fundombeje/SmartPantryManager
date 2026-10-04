package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Settings screen: currently one switch, for highlighting items that are expiring soon.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle(R.string.settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back arrow
        }

        SwitchCompat switchExpiry = findViewById(R.id.switchExpiry);
        switchExpiry.setChecked(AppSettings.isExpiryHighlightEnabled(this));
        // Save the choice as soon as the switch is toggled
        switchExpiry.setOnCheckedChangeListener((button, isChecked) ->
                AppSettings.setExpiryHighlightEnabled(this, isChecked));
    }

    /**
     * Makes the toolbar back arrow close this screen.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}