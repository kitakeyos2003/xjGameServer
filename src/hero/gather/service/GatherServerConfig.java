// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.service;

import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class GatherServerConfig extends AbsConfig {

    public String gatherDataPath;
    public String soulsDataPath;

    @Override
    public void init(final Element _xmlNode) throws Exception {
        Element dataPathE = _xmlNode.element("para");
        this.gatherDataPath = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("gather_data_dir");
        this.soulsDataPath = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("souls_data_dir");
    }
}
