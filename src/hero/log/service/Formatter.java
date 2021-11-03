// 
// Decompiled by Procyon v0.5.36
// 
package hero.log.service;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Formatter {

    private static String L_BRACKET;
    private static String R_BRACKET;
    private StringBuffer logBuffer;
    private static final SimpleDateFormat dateFormatter;
    private int count;

    static {
        Formatter.L_BRACKET = "[";
        Formatter.R_BRACKET = "]";
        dateFormatter = (SimpleDateFormat) DateFormat.getDateTimeInstance();
    }

    public Formatter() {
        this.logBuffer = new StringBuffer();
        this.count = 0;
        Formatter.dateFormatter.applyPattern("yyyy-MM-dd HH:mm:ss:SS");
    }

    public Formatter reset() {
        this.count = 0;
        this.logBuffer.delete(0, this.logBuffer.length());
        return this;
    }

    public Formatter append(final String _logUnit) {
        ++this.count;
        this.logBuffer.append(Formatter.L_BRACKET).append(_logUnit).append(Formatter.R_BRACKET);
        return this;
    }

    public Formatter append(final int _logUnit) {
        return this.append(String.valueOf(_logUnit));
    }

    public Formatter append(final float _logUnit) {
        return this.append(String.valueOf(_logUnit));
    }

    public Formatter append(final double _logUnit) {
        return this.append(String.valueOf(_logUnit));
    }

    public Formatter append(final short _logUnit) {
        return this.append(String.valueOf(_logUnit));
    }

    public Formatter append(final Date _date) {
        return this.append(Formatter.dateFormatter.format(_date));
    }

    public String flush() {
        return this.logBuffer.toString();
    }

    public int getCount() {
        return this.count;
    }
}
