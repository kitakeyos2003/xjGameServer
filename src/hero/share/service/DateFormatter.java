// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateFormatter {

    private static SimpleDateFormat dateFormatter;

    static {
        DateFormatter.dateFormatter = (SimpleDateFormat) DateFormat.getDateTimeInstance();
    }

    private DateFormatter() {
    }

    public static String currentTime() {
        return DateFormatter.dateFormatter.format(new Date());
    }

    public static String getStringTime(final String _format, final Date _date) {
        String date = "";
        SimpleDateFormat formatter = new SimpleDateFormat(_format);
        if (_date != null) {
            date = formatter.format(_date.getTime());
        }
        return date;
    }

    public static String format(final Date _date) {
        return DateFormatter.dateFormatter.format(_date);
    }
}
