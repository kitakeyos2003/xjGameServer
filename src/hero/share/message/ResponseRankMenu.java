// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import java.util.Iterator;
import hero.share.RankMenuField;
import java.util.Map;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseRankMenu extends AbsResponseMessage {

    private static Logger log;
    private Map<Byte, RankMenuField> rankMainMenuFieldList;

    static {
        ResponseRankMenu.log = Logger.getLogger((Class) ResponseRankMenu.class);
    }

    public ResponseRankMenu(final Map<Byte, RankMenuField> rankMainMenuFieldList) {
        this.rankMainMenuFieldList = rankMainMenuFieldList;
    }

    @Override
    protected void write() throws IOException {
        ResponseRankMenu.log.debug((Object) ("rankMainMenuFieldList size=" + this.rankMainMenuFieldList.size()));
        this.yos.writeByte(this.rankMainMenuFieldList.size());
        for (final RankMenuField rmf : this.rankMainMenuFieldList.values()) {
            this.yos.writeByte(rmf.id);
            this.yos.writeUTF(rmf.name);
            this.yos.writeByte(rmf.menuLevel);
            ResponseRankMenu.log.debug((Object) ("main id=" + rmf.id + ",name=" + rmf.name + ",level=" + rmf.menuLevel));
            boolean hasSecondMenu = rmf.childMenuList != null && rmf.childMenuList.size() > 0;
            this.yos.writeByte(hasSecondMenu);
            if (hasSecondMenu) {
                ResponseRankMenu.log.debug((Object) ("Second MenuList size=" + rmf.childMenuList.size()));
                this.yos.writeByte(rmf.childMenuList.size());
                for (final RankMenuField srmf : rmf.childMenuList) {
                    this.yos.writeByte(srmf.id);
                    this.yos.writeUTF(srmf.name);
                    this.yos.writeByte(srmf.menuLevel);
                    boolean hasChildMenu = srmf.childMenuList != null && srmf.childMenuList.size() > 0;
                    this.yos.writeByte(hasChildMenu);
                    if (hasChildMenu) {
                        ResponseRankMenu.log.debug((Object) ("third MenuList size=" + srmf.childMenuList.size()));
                        this.yos.writeByte(srmf.childMenuList.size());
                        for (final RankMenuField crmf : srmf.childMenuList) {
                            this.yos.writeByte(crmf.id);
                            this.yos.writeUTF(crmf.name);
                            this.yos.writeByte(crmf.menuLevel);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
