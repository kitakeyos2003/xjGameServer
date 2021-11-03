// 
// Decompiled by Procyon v0.5.36
// 
package hero.log.service;

import java.util.Date;
import java.text.SimpleDateFormat;

public class XmlFormatter {

    private static SimpleDateFormat dateFormatter;
    private StringBuffer logBuffer;
    private String rootElementName;
    private int count;

    public XmlFormatter(final String _rootElementName) {
        this.logBuffer = new StringBuffer();
        this.count = 0;
        XmlFormatter.dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        this.rootElementName = _rootElementName;
        this.logBuffer.append("<").append(_rootElementName).append(">");
    }

    public XmlFormatter reset() {
        this.count = 0;
        this.logBuffer.delete(0, this.logBuffer.length());
        return this;
    }

    public XmlFormatter append(final String _elementName, final String _value) {
        ++this.count;
        this.logBuffer.append("<").append(_elementName).append(">").append(_value).append("</").append(_elementName).append(">");
        return this;
    }

    public XmlFormatter append(final String _elementName, final int _logUnit) {
        return this.append(_elementName, String.valueOf(_logUnit));
    }

    public XmlFormatter append(final String _elementName, final float _logUnit) {
        return this.append(_elementName, String.valueOf(_logUnit));
    }

    public XmlFormatter append(final String _elementName, final double _logUnit) {
        return this.append(_elementName, String.valueOf(_logUnit));
    }

    public XmlFormatter append(final String _elementName, final short _logUnit) {
        return this.append(_elementName, String.valueOf(_logUnit));
    }

    public XmlFormatter append(final String _elementName, final Date _date) {
        return this.append(_elementName, XmlFormatter.dateFormatter.format(_date));
    }

    public String flush() {
        this.logBuffer.append("</").append(this.rootElementName).append(">");
        return this.logBuffer.toString();
    }

    public int getCount() {
        return this.count;
    }
}
