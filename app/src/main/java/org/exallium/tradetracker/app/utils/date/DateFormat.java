package org.exallium.tradetracker.app.utils.date;

import android.content.res.Resources;
import org.exallium.tradetracker.app.R;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateFormat {

    private static final DateTimeFormatter format = DateTimeFormat.forPattern("MMMMM dd yyyy");

    public static String toString(Resources resources, LocalDate date) {

        if (date.isEqual(LocalDate.now())) {
            return resources.getString(R.string.today);
        } else if (date.isEqual(LocalDate.now().minusDays(1))) {
            return resources.getString(R.string.yesterday);
        } else {
            return format.print(date);
        }

    }

    public static String toField(LocalDate date) {
        return format.print(date);
    }

    public static LocalDate fromString(String date) {
        try {
            return format.parseLocalDate(date);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

}
