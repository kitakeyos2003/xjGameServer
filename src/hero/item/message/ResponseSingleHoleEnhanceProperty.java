// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.EquipmentInstance;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseSingleHoleEnhanceProperty extends AbsResponseMessage {

    private static Logger log;
    private EquipmentInstance ei;

    static {
        ResponseSingleHoleEnhanceProperty.log = Logger.getLogger((Class) ResponseSingleHoleEnhanceProperty.class);
    }

    public ResponseSingleHoleEnhanceProperty(final EquipmentInstance ei) {
        this.ei = ei;
    }

    @Override
    protected void write() throws IOException {
        if (this.ei != null) {
            byte[][] detail = this.ei.getGeneralEnhance().detail;
            this.yos.writeByte(detail.length);
            for (int i = 0; i < detail.length; ++i) {
                ResponseSingleHoleEnhanceProperty.log.debug((Object) (String.valueOf(detail[i][0]) + "---" + detail[i][1] + " -- module=" + this.ei.getGeneralEnhance().getHoleModules(i) * 100.0f));
                this.yos.writeByte(i);
                this.yos.writeShort(this.ei.getGeneralEnhance().getYetSetJewel(i)[0]);
                this.yos.writeShort(this.ei.getGeneralEnhance().getYetSetJewel(i)[1]);
                this.yos.writeByte(detail[i][0]);
                this.yos.writeByte(detail[i][1]);
                this.yos.writeInt(this.ei.getGeneralEnhance().getHoleModules(i) * 100.0f);
            }
        } else {
            this.yos.writeShort(0);
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
