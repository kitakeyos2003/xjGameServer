// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.message;

import java.io.IOException;
import java.util.Iterator;
import hero.charge.FeeType;
import hero.charge.FeePointInfo;
import java.util.List;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class SendChargeList extends AbsResponseMessage {

    private static Logger log;
    private List<FeePointInfo> fpList;
    private List<FeeType> feeTypeList;

    static {
        SendChargeList.log = Logger.getLogger((Class) SendChargeList.class);
    }

    public SendChargeList(final List<FeePointInfo> _fpList, final List<FeeType> _feeTypeList) {
        this.fpList = _fpList;
        this.feeTypeList = _feeTypeList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte((byte) this.feeTypeList.size());
        for (final FeeType feeType : this.feeTypeList) {
            this.yos.writeByte(feeType.id);
            this.yos.writeUTF(feeType.name);
            this.yos.writeUTF(feeType.desc);
        }
        this.yos.writeByte((byte) this.fpList.size());
        for (final FeePointInfo info : this.fpList) {
            this.yos.writeByte(info.id);
            this.yos.writeUTF(info.fpcode);
            this.yos.writeByte(info.typeID);
            this.yos.writeUTF(info.name);
            this.yos.writeInt(info.price);
            this.yos.writeUTF(info.desc);
        }
        SendChargeList.log.info((Object) ("output size = " + String.valueOf(this.yos.size())));
    }
}
