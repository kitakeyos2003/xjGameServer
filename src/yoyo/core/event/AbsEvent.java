// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.event;

public abstract class AbsEvent<T> {

    protected String src;
    protected String dest;
    protected T context;

    public AbsEvent() {
    }

    public AbsEvent(final String source, final String destination, final T body) {
        this.src = source;
        this.dest = destination;
        this.context = body;
    }

    public String getSrc() {
        return this.src;
    }

    public void setSrc(final String src) {
        this.src = src;
    }

    public String getDest() {
        return this.dest;
    }

    public void setDest(final String dest) {
        this.dest = dest;
    }

    public T getContext() {
        return this.context;
    }

    public void setContext(final T coxt) {
        this.context = coxt;
    }
}
