// 
// Decompiled by Procyon v0.5.36
// 
package hero.log.service;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;

public class DateTime {

    private static Locale currentLocale;
    public static final int DEFAULT = 2;
    public static final int SHORT = 3;
    public static final int MEDIUM = 2;
    public static final int LONG = 1;
    public static final int FULL = 0;
    private static String result;
    private static DateFormat formatter;

    static {
        DateTime.currentLocale = new Locale("zh", "CN");
    }

    public DateTime() {
    }

    public DateTime(final Locale currentLocale) {
        DateTime.currentLocale = currentLocale;
    }

    public static String showDateStyles(final int style) {
        DateTime.formatter = DateFormat.getDateInstance(style, DateTime.currentLocale);
        return DateTime.result = DateTime.formatter.format(new Date());
    }

    public static String showtimeStyles(final int style) {
        DateTime.formatter = DateFormat.getTimeInstance(style, DateTime.currentLocale);
        return DateTime.result = DateTime.formatter.format(new Date());
    }

    public static String showBothStyles(final int style) {
        DateTime.formatter = DateFormat.getDateTimeInstance(style, style, DateTime.currentLocale);
        return DateTime.result = DateTime.formatter.format(new Date());
    }

    public static String getCurrentTime() {
        DateTime.formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return DateTime.result = DateTime.formatter.format(new Date());
    }

    public static String getCurrentTime2() {
        DateTime.formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return DateTime.result = DateTime.formatter.format(new Date());
    }

    public static String getTime(final long time) {
        DateTime.formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return DateTime.result = DateTime.formatter.format(c.getTime());
    }

    public static void main(final String[] args) {
    }
}
