// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.net.parse;

import java.util.HashMap;

public abstract class XmlParamModel {

    public XmlParamModel(final String _param) {
        this.parse(_param);
        this.process();
    }

    public XmlParamModel(final HashMap<String, String> _param) {
        this.parse(_param);
        this.process();
    }

    public abstract void process();

    protected void parse(final String _param) {
    }

    protected void parse(final HashMap<String, String> _param) {
    }
}
