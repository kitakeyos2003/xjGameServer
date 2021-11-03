// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.net;

import java.util.HashMap;

public class HttpRequest {

    private String method;
    private String uri;
    public String briefURI;
    private String content;
    private HashMap<String, String> uriParams;

    public HttpRequest() {
        this.uriParams = new HashMap<String, String>();
    }

    public String getRequestMethod() {
        return this.method;
    }

    public void setRequestMethod(final String method) {
        this.method = method;
    }

    public String getRequestURI() {
        return this.uri;
    }

    public void setRequestURI(final String uri) {
        this.uri = uri;
    }

    public void setBriefRequestURI(final String _briefURI) {
        this.briefURI = _briefURI;
    }

    public String getBriefRequestURI() {
        return this.briefURI;
    }

    public final void setURIParam(final String key, final String value) {
        if (key != null) {
            if (value == null) {
                this.uriParams.remove(key);
            } else {
                this.uriParams.put(key, value);
            }
        }
    }

    public final String getURIParam(final String key) {
        return (key == null) ? null : this.uriParams.get(key);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.method).append(" ");
        buffer.append(this.uri).append(" ");
        buffer.append("").append("\r\n");
        buffer.append(super.toString());
        return buffer.toString();
    }

    public void addParam(final String _name, final String _value) {
        this.uriParams.put(_name, _value);
    }

    public String getParam(final String _name) {
        if (this.uriParams.containsKey(_name)) {
            return this.uriParams.get(_name);
        }
        return null;
    }

    public void setContent(final String _content) {
        this.content = _content;
    }

    public String getContent() {
        return this.content;
    }

    public HashMap<String, String> getParamsMap() {
        return this.uriParams;
    }
}
