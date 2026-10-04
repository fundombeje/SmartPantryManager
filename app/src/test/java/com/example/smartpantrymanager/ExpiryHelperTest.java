package com.example.smartpantrymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantrymanager.logic.ExpiryHelper;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpiryHelperTest {

    /** Builds a yyyy-MM-dd string for today plus the given number of days. */
    private static String dateInDays(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.getTime());
    }

    @Test
    public void missingOrInvalidDate_hasNoExpiry() {
        assertEquals(ExpiryHelper.NO_DATE, ExpiryHelper.daysUntilExpiry(null));
        assertEquals(ExpiryHelper.NO_DATE, ExpiryHelper.daysUntilExpiry(""));
        assertEquals(ExpiryHelper.NO_DATE, ExpiryHelper.daysUntilExpiry("not a date"));
        assertFalse(ExpiryHelper.isExpiringSoon(null));
    }

    @Test
    public void todayAndTomorrow_countDaysCorrectly() {
        assertEquals(0, ExpiryHelper.daysUntilExpiry(dateInDays(0)));
        assertEquals(1, ExpiryHelper.daysUntilExpiry(dateInDays(1)));
    }

    @Test
    public void pastDate_isExpiredAndExpiringSoon() {
        assertEquals(-1, ExpiryHelper.daysUntilExpiry(dateInDays(-1)));
        assertTrue(ExpiryHelper.isExpiringSoon(dateInDays(-1)));
    }

    @Test
    public void withinThreeDays_isExpiringSoon() {
        assertTrue(ExpiryHelper.isExpiringSoon(dateInDays(3)));
    }

    @Test
    public void farFuture_isNotExpiringSoon() {
        assertFalse(ExpiryHelper.isExpiringSoon(dateInDays(10)));
    }
}