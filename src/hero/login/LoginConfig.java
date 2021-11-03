// 
// Decompiled by Procyon v0.5.36
// 
package hero.login;

import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class LoginConfig extends AbsConfig {

    public String rmi_url;
    public int rmi_port;

    @Override
    public void init(final Element _xmlNode) throws Exception {
        Element element = _xmlNode.element("login");
        this.rmi_port = Integer.parseInt(element.elementTextTrim("port"));
        this.rmi_url = new StringBuffer("//").append(element.elementTextTrim("host")).append(":").append(this.rmi_port).append("/").append(element.elementTextTrim("rmi_object_name")).toString();
    }
}
