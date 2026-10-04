package com.example.smartpantrymanager.logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Works out how close a pantry item is to its expiry date. */
public final class ExpiryHelper {

    /** Items expiring within this many days count as "expiring soon". */
    public static final int EXPIRING_SOON_DAYS = 3;

    /** Returned when an item has no (valid) expiry date. */
    public static final int NO_DATE = Integer.MAX_VALUE;

    private ExpiryHelper() { }

    /** Days from today until the date (yyyy-MM-dd): 0 = today, negative = already expired. */
    public static int daysUntilExpiry(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return NO_DATE;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setLenient(false); // reject impossible dates like 2026-13-45
            Date expiry = format.parse(isoDate);
            if (expiry == null) {
                return NO_DATE;
            }

            // Midnight today, so we compare whole days, not hours
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long diffMillis = expiry.getTime() - today.getTimeInMillis();
            return (int) Math.round(diffMillis / (24.0 * 60 * 60 * 1000));
        } catch (ParseException e) {
            return NO_DATE;
        }
    }

    /** True if the item has expired or will expire within EXPIRING_SOON_DAYS. */
    public static boolean isExpiringSoon(String isoDate) {
        int days = daysUntilExpiry(isoDate);
        return days != NO_DATE && days <= EXPIRING_SOON_DAYS;
    }
}