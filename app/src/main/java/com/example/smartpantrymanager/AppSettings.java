package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Reads and saves the user's settings (kept on the device, survives app restarts).
 */
public final class AppSettings {

    private static final String PREFS_NAME = "smart_pantry_settings";
    private static final String KEY_HIGHLIGHT_EXPIRING = "highlight_expiring";

    private AppSettings() {
    }

    /**
     * Whether expiring items should be highlighted in red. On by default.
     */
    public static boolean isExpiryHighlightEnabled(Context context) {
        return prefs(context).getBoolean(KEY_HIGHLIGHT_EXPIRING, true);
    }

    public static void setExpiryHighlightEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_HIGHLIGHT_EXPIRING, enabled).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}