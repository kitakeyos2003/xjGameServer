// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.service;

import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class ManufactureServerConfig extends AbsConfig {

    public String[] dataPath;

    public ManufactureServerConfig() {
        this.dataPath = new String[4];
    }

    @Override
    public void init(final Element _xmlNode) throws Exception {
        Element dataPathE = _xmlNode.element("para");
        this.dataPath[0] = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("dispenser_data_dir");
        this.dataPath[1] = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("jeweler_data_dir");
        this.dataPath[2] = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("craftsman_data_dir");
        this.dataPath[3] = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("blacksmith_data_dir");
    }
}
