package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.PantryItemDao;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * Form used both to add a new pantry item and to edit an existing one.
 */
public class AddEditItemActivity extends AppCompatActivity {

    /**
     * Key for the Intent extra that carries the id of the item being edited.
     */
    public static final String EXTRA_ITEM_ID = "item_id";
    private static final int NO_ID = -1;

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiry;
    private Spinner spinnerUnit;

    /**
     * NO_ID means we are adding a new item; any other value means we are editing.
     */
    private int itemId = NO_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back arrow
        }

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        MaterialButton buttonSave = findViewById(R.id.buttonSave);
        MaterialButton buttonClearDate = findViewById(R.id.buttonClearDate);

        // Fill the unit dropdown from the string-array in strings.xml
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this, R.array.units, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        editExpiry.setOnClickListener(v -> showDatePicker());
        buttonClearDate.setOnClickListener(v -> editExpiry.setText(""));
        buttonSave.setOnClickListener(v -> saveItem());

        // Read the Intent that started this screen: an id means "edit", no id means "add"
        itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, NO_ID);
        if (itemId != NO_ID) {
            setTitle("Edit ingredient");
            loadItem();
        } else {
            setTitle("Add ingredient");
        }
    }

    /**
     * Makes the toolbar back arrow close this screen.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void loadItem() {
        AppDatabase.databaseExecutor.execute(() -> {
            PantryItem item = AppDatabase.getInstance(getApplicationContext())
                    .pantryItemDao().getById(itemId);
            runOnUiThread(() -> {
                if (item == null) {
                    finish(); // item no longer exists
                    return;
                }
                editName.setText(item.name);
                editQuantity.setText(PantryAdapter.formatQuantity(item.quantity));
                editExpiry.setText(item.expiryDate);

                String[] units = getResources().getStringArray(R.array.units);
                for (int i = 0; i < units.length; i++) {
                    if (units[i].equals(item.unit)) {
                        spinnerUnit.setSelection(i);
                        break;
                    }
                }
            });
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // If a date is already set, open the picker on that date
        String current = editExpiry.getText() == null ? "" : editExpiry.getText().toString();
        if (current.matches("\\d{4}-\\d{2}-\\d{2}")) {
            year = Integer.parseInt(current.substring(0, 4));
            month = Integer.parseInt(current.substring(5, 7)) - 1;
            day = Integer.parseInt(current.substring(8, 10));
        }

        new DatePickerDialog(this, (view, y, m, d) ->
                editExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                year, month, day).show();
    }

    /**
     * Validates the form, if all is valid then saves the item.
     */
    private void saveItem() {
        String name = editName.getText() == null ? "" : editName.getText().toString().trim();
        // Some regional settings use a comma as the decimal separator, so accept both
        String quantityText = editQuantity.getText() == null ? ""
                : editQuantity.getText().toString().trim().replace(',', '.');
        String expiryText = editExpiry.getText() == null ? "" : editExpiry.getText().toString().trim();

        layoutName.setError(null);
        layoutQuantity.setError(null);
        boolean valid = true;

        if (name.isEmpty()) {
            layoutName.setError("Please enter an ingredient name");
            valid = false;
        } else if (!name.matches(".*[A-Za-z].*")) {
            layoutName.setError("The name must contain letters");
            valid = false;
        }

        double quantity = 0;
        if (quantityText.isEmpty()) {
            layoutQuantity.setError("Please enter a quantity");
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (Double.isNaN(quantity) || Double.isInfinite(quantity) || quantity <= 0) {
                    layoutQuantity.setError("Quantity must be greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                layoutQuantity.setError("Please enter a valid number");
                valid = false;
            }
        }

        if (!valid) {
            return; // stay on the form so the user can correct the errors
        }

        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = expiryText.isEmpty() ? null : expiryText; // expiry is optional
        persistItem(name, quantity, unit, expiry);
    }

    /**
     * Inserts a new item or updates the existing one, then closes the screen.
     */
    private void persistItem(String name, double quantity, String unit, String expiry) {
        AppDatabase.databaseExecutor.execute(() -> {
            PantryItemDao dao = AppDatabase.getInstance(getApplicationContext()).pantryItemDao();

            PantryItem item = new PantryItem(name, quantity, unit, expiry);
            if (itemId == NO_ID) {
                dao.insert(item);          // Create
            } else {
                item.id = itemId;          // Room's @Update matches on the primary key
                dao.update(item);          // Update
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
                finish(); // MainActivity.onResume() will reload the list
            });
        });
    }
}