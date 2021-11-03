// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.tools.database;

import org.logicalcobwebs.proxool.ProxoolFacade;
import java.util.Properties;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class DBConfig extends AbsConfig {

    String dbDriverName;
    String dbPoolName;
    String proxDriverName;
    String proxpoolName;

    @Override
    public void init(final Element element) throws Exception {
        Element eDb = element.element("db");
        this.proxDriverName = eDb.elementTextTrim("prox_driver");
        this.dbDriverName = eDb.elementTextTrim("db_driver");
        Class.forName(this.proxDriverName);
        Class.forName(this.dbDriverName);
        this.dbPoolName = eDb.elementTextTrim("pool_name");
        this.proxpoolName = String.valueOf(eDb.elementTextTrim("proxool_name")) + "." + this.dbPoolName;
        String URL = String.valueOf(this.proxpoolName) + ":" + this.proxDriverName + ":" + eDb.elementTextTrim("url");
        Properties info = new Properties();
        info.setProperty("user", eDb.elementTextTrim("user_name"));
        info.setProperty("password", eDb.elementTextTrim("password"));
        info.setProperty("proxool.maximum-connection-count", eDb.elementTextTrim("maximum-connection-count"));
        info.setProperty("proxool.minimum-connection-count", eDb.elementTextTrim("minimum-connection-count"));
        info.setProperty("proxool.maximum-active-time", eDb.elementTextTrim("maximum-active-time"));
        info.setProperty("proxool.simultaneous-build-throttle", eDb.elementTextTrim("simultaneous-build-throttle"));
        info.setProperty("proxool.house-keeping-test-sql", eDb.elementTextTrim("house-keeping-test-sql"));
        info.setProperty("proxool.house-keeping-sleep-time", eDb.elementTextTrim("house-keeping-sleep-time"));
        info.setProperty("proxool.prototype-count", eDb.elementTextTrim("prototype-count"));
        ProxoolFacade.registerConnectionPool(URL, info);
    }
}
