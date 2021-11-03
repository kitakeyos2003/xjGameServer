// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.service;

import java.util.Calendar;
import java.text.DateFormat;
import java.util.GregorianCalendar;

public class TimeTest {

    public static void main(final String[] args) {
        Calendar calendar = new GregorianCalendar(2008, 5, 5, 16, 8, 0);
        Calendar calendar2 = new GregorianCalendar(2008, 5, 1, 13, 13, 0);
        DateFormat df = DateFormat.getDateTimeInstance();
        String backupFileName = df.format(calendar.getTime());
        String backupFileName2 = df.format(calendar2.getTime());
        int days = (int) ((calendar.getTimeInMillis() - calendar2.getTimeInMillis()) / 86400000L);
        int hour = (int) ((calendar.getTimeInMillis() - calendar2.getTimeInMillis()) % 86400000L) / 3600000;
        int minute = (int) ((calendar.getTimeInMillis() - calendar2.getTimeInMillis()) % 86400000L) % 3600000 / 60000;
        System.out.println("\u8ddd\u79bb\u91cd\u7f6e" + days + "\u5929" + hour + "\u5c0f\u65f6" + minute + "\u5206");
    }
}
